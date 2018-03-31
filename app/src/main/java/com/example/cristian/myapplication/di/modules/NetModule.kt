package com.example.cristian.myapplication.di.modules

import android.content.Context
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.net.MilkClient
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetModule {
    @Provides
    @Singleton
    fun provideRetrofit(context: Context): Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .baseUrl(context.getString(R.string.base_url))
            .build()

    @Provides
    @Singleton
    fun provideMilkClient(retrofit: Retrofit): MilkClient =
            retrofit.create(MilkClient::class.java)
}