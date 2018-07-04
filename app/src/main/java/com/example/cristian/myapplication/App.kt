package com.example.cristian.myapplication

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.support.multidex.MultiDexApplication
import com.example.cristian.myapplication.di.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App: MultiDexApplication(),HasActivityInjector{

    @Inject
    lateinit var injector:DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = injector

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)

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

    companion object {
        const val CHANNEL_ID = "ganko.channel.notification"
    }

}