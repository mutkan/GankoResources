package com.ceotic.ganko

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.support.multidex.MultiDexApplication
import com.couchbase.lite.*
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.di.AppInjector
import com.ceotic.ganko.util.andEx
import com.ceotic.ganko.util.equalEx
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import java.net.URI
import javax.inject.Inject

class App: MultiDexApplication(),HasActivityInjector{

    @Inject
    lateinit var injector:DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var session:UserSession

    @Inject
    lateinit var db: Database

    override fun activityInjector(): AndroidInjector<Activity> = injector

    var replicator: Replicator? = null

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
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    fun startReplicator(){
        if(session.userId != null){
            val config = ReplicatorConfiguration(db, URLEndpoint(URI(getString(R.string.url_sync))))
            config.replicatorType = ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL
            config.isContinuous = true

            config.channels = mutableListOf(session.userId, "account_${session.userId}")
            replicator = Replicator(config)
            replicator?.start()


            db.addDocumentChangeListener(session.userId){
                val doc = db.getDocument(it.documentID)
                session.plan = doc.getString("plan")
                session.planDate = doc.getDate("ultimoPago")
            }

        }
    }

    companion object {
        const val CHANNEL_ID = "ganko.channel.notification"
    }

}