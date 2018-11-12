package com.ceotic.ganko.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideContext(app: Application): Context = app

    @Singleton
    @Provides
    fun providePreference(context: Context): SharedPreferences =
            context.getSharedPreferences("ganko", 0)


}
