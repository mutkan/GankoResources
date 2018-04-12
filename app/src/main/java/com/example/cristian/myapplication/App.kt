package com.example.cristian.myapplication

import android.app.Activity
import android.app.Application
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

}