package com.ceotic.ganko

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.support.multidex.MultiDexApplication
import androidx.work.*
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.di.AppInjector
import com.ceotic.ganko.util.*
import com.ceotic.ganko.work.NotificationWork
import com.couchbase.lite.*
import com.couchbase.lite.Dictionary
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
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
    lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        workManager = WorkManager.getInstance()
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

    fun stopReplicator(){
        replicator?.stop()
        replicator = null
        if (changeToken != null) db.removeChangeListener(changeToken)
        changeToken = null
    }

    private fun notificationListener() {
        val device = session.device
        val next = Date().add(Calendar.DATE, 5)
        val exp = ("activa" equalEx true
                andEx ("fechaProxima" gt Date())
                andEx( "fechaProxima" lt next!! )
                andEx ("type" equalEx TYPE_ALARM)
                andEx Expression.negated(
                ArrayExpression.any(ArrayExpression.variable("d")).`in`(Expression.property("device"))
                        .satisfies(ArrayExpression.variable("d.device").equalTo(Expression.longValue(device))))
                )

        addNotification(device, exp)
                .subscribeOn(Schedulers.io())
                .subscribe()

        val expDis = ("activa" equalEx false
                andEx ("fechaProxima" gt Date())
                andEx( "fechaProxima" lt next )
                andEx ("type" equalEx TYPE_ALARM)
                andEx ArrayExpression.any(ArrayExpression.variable("d")).`in`(Expression.property("device"))
                .satisfies(ArrayExpression.variable("d.device").equalTo(Expression.longValue(device)))
                )

        cancelNotification(device, expDis)
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun addNotification(device: Long, exp: Expression): Single<Unit> {
        var now: Long = 0

        return Single.create<ResultSet> { emitter ->
            val query = QueryBuilder
                    .select(SelectResult.all(), SelectResult.expression(Meta.id))
                    .from(DataSource.database(db))
                    .where(exp)
            emitter.onSuccess(query.execute())
        }
                .doOnSuccess { now = Date().time }
                .flatMapObservable { it.toObservable() }
                .map {
                    val dic = it.getDictionary("ganko-database")
                    val id = it.getString("id")
                    dic to id
                }
                .filter { it.first != null }
                .map { (doc, id) ->
                    val alarma = doc.getInt("alarma")
                    val fechaProxima = mapper.convertValue<Date>(doc.getString("fechaProxima"))
                    val reference = doc.getString("reference")
                    val titulo = doc.getString("titulo")
                    val descripcion = doc.getString("descripcion")
                    val bvn = doc.getDictionary("bovino")
                    val gp = doc.getDictionary("grupo")
                    val farm = doc.getString("idFinca")

                    val milis = (fechaProxima.time - now) / 60000

                    val type = when (alarma) {
                        in ALARM_2_MONTHS..ALARM_12_MONTHS, ALARM_VACCINE -> NotificationWork.TYPE_VACCINES
                        ALARM_18_MONTHS, in ALARM_SECADO..ALARM_DIAGNOSIS, ALARM_REJECT_DIAGNOSIS_3 -> NotificationWork.TYPE_REPRODUCTIVE
                        ALARM_HEALTH -> NotificationWork.TYPE_HEALTH
                        ALARM_MANAGE -> NotificationWork.TYPE_MANAGEMENT
                        else -> NotificationWork.TYPE_MEADOW
                    }
                    val (title, des) = when (alarma) {
                        in ALARM_18_MONTHS..ALARM_12_MONTHS -> titulo to descripcion
                        in ALARM_SECADO..ALARM_DIAGNOSIS -> {
                            titulo to descripcion + ", Bovino " + (bvn?.getString("codigo"))
                        }
                        in ALARM_HEALTH..ALARM_MANAGE -> {
                            val info = when {
                                bvn != null -> " - Bovino ${bvn.getString("codigo")}"
                                gp != null -> " - Grupo ${gp.getString("nombre")}"
                                else -> ""
                            }
                            titulo to descripcion + info
                        }
                        in ALARM_MEADOW_OCUPATION..ALARM_MEADOW_EXIT -> titulo to descripcion
                        else -> {
                            titulo to descripcion + ", Bovino " + bvn?.getString("codigo")
                        }
                    }

                    val data: Data = Data.Builder()
                            .putString(NotificationWork.ARG_ID, reference)
                            .putString(NotificationWork.ARG_TITLE, title)
                            .putString(NotificationWork.ARG_DESCRIPTION, des)
                            .putInt(NotificationWork.ARG_TYPE, type)
                            .putString(NotificationWork.ARG_FARM, farm)
                            .build()

                    val notificationWork = OneTimeWorkRequestBuilder<NotificationWork>()
                            .setInitialDelay(milis, TimeUnit.MINUTES)
                            .setInputData(data)
                            .build()

                    id to notificationWork
                }
                .doOnNext {workManager.enqueue(it.second)}
                .map { it.first to it.second.id }
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
                .toList()
                .flatMap {
                    Single.timer(18, TimeUnit.SECONDS)
                            .flatMap { addNotification(device, exp) }
                }


    }

    private fun cancelNotification(device: Long, exp: Expression): Single<Unit> {
        return Single.create<ResultSet> { emitter ->
            val query = QueryBuilder
                    .select(SelectResult.all(), SelectResult.expression(Meta.id))
                    .from(DataSource.database(db))
                    .where(exp)
            emitter.onSuccess(query.execute())
        }
                .flatMapObservable { it.toObservable() }
                .map { it.getDictionary("ganko-database") to it.getString("id") }
                .filter { it.first != null }
                .map { (doc, id) ->
                    val devices = doc.getArray("device")
                    val idx = devices.indexOfFirst {
                        val i = it as Dictionary
                        it.getLong("device") == device
                    }

                    if (idx > -1) {
                        val uuid = devices.getDictionary(idx).getString("uuid")
                        NotificationWork.cancelNotificationById(UUID.fromString(uuid))
                    }
                    id to idx
                }
                .flatMapSingle { (id, idx) ->
                    if (idx > -1) Single.fromCallable {
                        val doc = db.getDocument(id).toMutable()
                        val devices = doc.getArray("device")
                        devices.remove(idx)
                        doc.setArray("device", devices)
                        db.save(doc)
                    }
                    else Single.just(Unit)
                }
                .toList()
                .map { Unit }
                .flatMap {
                    Single.timer(25, TimeUnit.SECONDS)
                            .flatMap { cancelNotification(device, exp) }
                }

    }


    companion object {
        const val CHANNEL_ID = "ganko.channel.notification"
    }

}
