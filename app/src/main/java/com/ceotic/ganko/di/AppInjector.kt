package com.ceotic.ganko.di

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.App
import com.ceotic.ganko.di.components.DaggerAppComponent
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector

class AppInjector {

    companion object {
        fun init(app: App){
            DaggerAppComponent.builder()
                    .application(app)
                    .build()
                    .inject(app)
            app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
                override fun onActivityPaused(activity: Activity?) {   }

                override fun onActivityResumed(activity: Activity?) {}

                override fun onActivityStarted(activity: Activity?) {}

                override fun onActivityDestroyed(activity: Activity?) {}

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

                override fun onActivityStopped(activity: Activity?) {}

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    handlerActivity(activity)
                }

            })
        }
        private fun handlerActivity(activity: Activity){
            if (activity is Injectable || activity is HasSupportFragmentInjector){
                AndroidInjection.inject(activity)
            }
            (activity as AppCompatActivity).supportFragmentManager
                    .registerFragmentLifecycleCallbacks(object: FragmentManager.FragmentLifecycleCallbacks(){
                        override fun onFragmentAttached(fm: FragmentManager?, f: Fragment?, context: Context?) {
                            if(f is Injectable || f is HasSupportFragmentInjector){
                                AndroidSupportInjection.inject(f)
                            }
                        }
                    }, true)
        }
    }

}