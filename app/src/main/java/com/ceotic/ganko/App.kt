package com.ceotic.ganko

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.support.multidex.MultiDexApplication
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.di.AppInjector
import com.ceotic.ganko.util.andEx
import com.ceotic.ganko.util.equalEx
import com.ceotic.ganko.util.gt
import com.ceotic.ganko.work.NotificationWork
import com.couchbase.lite.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.schedule


class App : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var session: UserSession

    @Inject
    lateinit var db: Database

    @Inject
    lateinit var mapper: ObjectMapper

    override fun activityInjector(): AndroidInjector<Activity> = injector

    private var replicator: Replicator? = null
    private var changeToken: ListenerToken? = null

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        createNotificationChannel()
        startReplicator()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    fun startReplicator() {
        if (session.userId != null) {
            val config = ReplicatorConfiguration(db, URLEndpoint(URI(getString(R.string.url_sync))))
            config.replicatorType = ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL
            config.isContinuous = true

            val channels = mutableListOf("account_${session.userId}")
            val planValid = session.validatePlanDate()
            if (planValid.first)
                channels.add(session.userId!!)

            config.channels = channels
            replicator = Replicator(config)
            replicator?.start()

            notificationListener()

            changeToken = db.addDocumentChangeListener(session.userId) {
                val doc = db.getDocument(it.documentID)
                session.plan = doc.getString("plan")
                session.planDate = doc.getDate("ultimoPago")

                replicator?.stop()
                replicator = null
                if (changeToken != null) db.removeChangeListener(changeToken)
                changeToken = null

                Timer("starRep", false).schedule(300) {
                    startReplicator()
                }
            }


        }
    }

    private fun notificationListener() {
        val device = session.device
        val exp = ("activa" equalEx true
                andEx ("fechaProxima" gt Date())
                andEx ("type" equalEx TYPE_ALARM)
                andEx Expression.negated(
                ArrayExpression.any(ArrayExpression.variable("d")).`in`(Expression.property("device"))
                        .satisfies(ArrayExpression.variable("d.device").equalTo(Expression.value(device))))
                )

        var now: Long = 0
        Observable.create<ResultSet> { emitter ->
            QueryBuilder
                    .select(SelectResult.all(), SelectResult.expression(Meta.id))
                    .from(DataSource.database(db))
                    .where(exp)
                    .addChangeListener {
                        emitter.onNext(it.results)
                    }
        }
                .doOnNext { now = Date().time }
                .flatMap {
                    it.toObservable()
                }
                .map { it.getDictionary("ganko-database").toMap() to it.getString("id") }
                .map {
                    it.first["_id"] = it.second
                    mapper.convertValue<Alarm>(it.first)
                }
                .distinct { it._id }
                .map { a ->
                    val milis = a.fechaProxima!!.time - now
                    val reference = a.reference!!
                    val alarm = a.alarma
                    val type = when (alarm) {
                        in ALARM_2_MONTHS..ALARM_12_MONTHS, ALARM_VACCINE -> NotificationWork.TYPE_VACCINES
                        ALARM_18_MONTHS, in ALARM_SECADO..ALARM_DIAGNOSIS, ALARM_REJECT_DIAGNOSIS_3 -> NotificationWork.TYPE_REPRODUCTIVE
                        ALARM_HEALTH -> NotificationWork.TYPE_HEALTH
                        ALARM_MANAGE -> NotificationWork.TYPE_MANAGEMENT
                        else -> NotificationWork.TYPE_MEADOW
                    }
                    val (title, des) = when (alarm) {
                        in ALARM_18_MONTHS..ALARM_12_MONTHS -> a.titulo to a.descripcion
                        in ALARM_SECADO..ALARM_DIAGNOSIS -> {
                            val bvn = a.bovino
                            a.titulo to a.descripcion + ", Bovino " + bvn!!.codigo
                        }
                        in ALARM_HEALTH..ALARM_MANAGE -> {
                            val bvn = a.bovino
                            val gp = a.grupo
                            val info = when {
                                bvn != null -> " - Bovino ${bvn.codigo}"
                                gp != null -> " - Grupo ${gp.nombre}"
                                else -> ""
                            }
                            a.titulo to a.descripcion + info
                        }
                        in ALARM_MEADOW_OCUPATION..ALARM_MEADOW_EXIT -> a.titulo to a.descripcion
                        else -> {
                            val bvn = a.bovino!!
                            a.titulo to a.descripcion + ", Bovino " + bvn.codigo
                        }
                    }
                    val uuid = NotificationWork.notify(type, title!!, des!!, reference, milis, TimeUnit.MILLISECONDS)
                    a._id!! to uuid


                }
                .flatMapSingle { (id, uuid) ->
                    val doc = db.getDocument(id).toMutable()
                    val devices = doc.getArray("device")
                    val dev = MutableDictionary()
                    dev.setLong("device", device)
                    dev.setString("uuid", "$uuid")
                    devices.addDictionary(dev)
                    doc.setArray("device", devices)
                    Single.fromCallable { db.save(doc) }
                }
                .subscribeOn(Schedulers.io())
                .subscribe()

        val expDis = ("activa" equalEx false
                andEx ("fechaProxima" gt Date())
                andEx ("type" equalEx TYPE_ALARM)
                andEx ArrayExpression.any(ArrayExpression.variable("d")).`in`(Expression.property("device"))
                .satisfies(ArrayExpression.variable("d.device").equalTo(Expression.value(device)))
                )

        Observable.create<ResultSet> { emitter ->
            QueryBuilder
                    .select(SelectResult.all())
                    .from(DataSource.database(db))
                    .where(expDis)
                    .addChangeListener {
                        emitter.onNext(it.results)
                    }
        }
                .flatMap { it.toObservable() }
                .map { it.getDictionary("ganko-database").toMap() }
                .map {mapper.convertValue<Alarm>(it)}
                .map { it.device }
                .flatMap { it.toObservable() }
                .filter { it.device == device }
                .map {
                    val uuid = it.uuid
                    NotificationWork.cancelNotificationById(UUID.fromString(uuid))
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    companion object {
        const val CHANNEL_ID = "ganko.channel.notification"
    }

}
