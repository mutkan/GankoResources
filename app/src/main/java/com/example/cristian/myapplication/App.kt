package com.example.cristian.myapplication

import android.app.Application
import com.example.cristian.myapplication.di.AppInjector

class App: Application(){

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

}