package com.example.cristian.myapplication.di.modules

import android.content.Context
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.net.*
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

    @Provides
    @Singleton
    fun provideLoginClient(retrofit: Retrofit): LoginClient =
            retrofit.create(LoginClient::class.java)

    @Provides
    @Singleton
    fun provideFeedClient(retrofit: Retrofit): FeedClient =
            retrofit.create(FeedClient::class.java)

    @Provides
    @Singleton
    fun provideHealthClient(retrofit: Retrofit): HealthClient =
            retrofit.create(HealthClient::class.java)

    @Provides
    @Singleton
    fun provideManageClient(retrofit: Retrofit): ManageClient =
            retrofit.create(ManageClient::class.java)

}