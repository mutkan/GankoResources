package com.ceotic.ganko.di.components

import android.app.Application
import com.ceotic.ganko.App
import com.ceotic.ganko.di.modules.AppModule
import com.ceotic.ganko.di.modules.CouchModule
import com.ceotic.ganko.di.modules.NetModule
import com.ceotic.ganko.di.modules.ViewModelModule
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
    ViewModelModule::class,
    CouchModule::class
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