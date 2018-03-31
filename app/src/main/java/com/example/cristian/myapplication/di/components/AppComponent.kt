package com.example.cristian.myapplication.di.components

import android.app.Application
import com.example.cristian.myapplication.App
import com.example.cristian.myapplication.di.modules.AppModule
import com.example.cristian.myapplication.di.modules.NetModule
import com.example.cristian.myapplication.di.modules.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ActivityComponents::class,
    AppModule::class,
    NetModule::class,
    ViewModelModule::class
])
interface AppComponent {

    fun inject(app: App)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }


}