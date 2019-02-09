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
import com.ceotic.ganko.util.containsEx
import com.ceotic.ganko.util.equalEx
import com.ceotic.ganko.util.gt
import com.ceotic.ganko.work.NotificationWork
import com.couchbase.lite.*
import com.couchbase.lite.Dictionary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.Observable
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
                andEx Expression.negated("device" containsEx device)
                )

        var now: Long = 0
        Observable.create<ResultSet> { emitter ->
            QueryBuilder
                    .select(SelectResult.all())
                    .from(DataSource.database(db))
                    .where(exp)
                    .addChangeListener {
                        emitter.onNext(it.results)
                    }
        }
                .doOnNext { now = Date().time }
                .flatMap { it.toObservable() }
                .map { it.getDictionary("ganko-database") }
                .map {
                    val milis = it.getDate("fechaProxima").time - now
                    val reference = it.getString("reference")
                    val alarm = it.getInt("alarma")
                    val type = when (alarm) {
                        in ALARM_2_MONTHS..ALARM_12_MONTHS, ALARM_VACCINE -> NotificationWork.TYPE_VACCINES
                        ALARM_18_MONTHS, in ALARM_SECADO..ALARM_DIAGNOSIS, ALARM_REJECT_DIAGNOSIS_3 -> NotificationWork.TYPE_REPRODUCTIVE
                        ALARM_HEALTH -> NotificationWork.TYPE_HEALTH
                        ALARM_MANAGE -> NotificationWork.TYPE_MANAGEMENT
                        else -> NotificationWork.TYPE_MEADOW
                    }
                    val (title, des) = when (alarm) {
                        in ALARM_18_MONTHS..ALARM_12_MONTHS -> it.getString("titulo") to it.getString("descripcion")
                        in ALARM_SECADO..ALARM_DIAGNOSIS -> {
                            val bvn = it.getDictionary("bovino")
                            it.getString("titulo") to it.getString("descripcion") + ", Bovino " + bvn.getString("codigo")
                        }
                        in ALARM_HEALTH..ALARM_MANAGE -> {
                            val bvn = it.getDictionary("bovino")
                            val gp = it.getDictionary("grupo")
                            val info = when {
                                bvn != null -> " - Bovino ${bvn.getString("codigo")}"
                                gp != null -> " - Grupo ${gp.getString("nombre")}"
                                else -> ""
                            }
                            it.getString("titulo") to it.getString("descripcion") + info
                        }
                        in ALARM_MEADOW_OCUPATION..ALARM_MEADOW_EXIT -> it.getString("titulo") to it.getString("descripcion")
                        else -> {
                            val bvn = it.getDictionary("bovino")
                            it.getString("titulo") to it.getString("descripcion") + ", Bovino " + bvn.getString("codigo")
                        }
                    }
                    NotificationWork.notify(type, title, des, reference, milis, TimeUnit.MILLISECONDS)
                }
                .subscribeOn(Schedulers.io())
                .subscribe()

        val expDis = ("activa" equalEx false
                andEx ("fechaProxima" gt Date())
                andEx ("type" equalEx TYPE_ALARM)
                andEx ("device" containsEx device)
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
                .map { it.getDictionary("ganko-database") }
                .map { it.getArray("device") as Array<Dictionary> }
                .flatMap { it.toObservable() }
                .filter { it.getLong("device") == device }
                .map {
                    val uuid = it.getString("uuid")
                    NotificationWork.cancelNotificationById(UUID.fromString(uuid))
                }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    companion object {
        const val CHANNEL_ID = "ganko.channel.notification"
    }

}
