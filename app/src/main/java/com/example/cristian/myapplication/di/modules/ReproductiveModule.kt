package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.bovine.reproductive.ListServiceFragment
import com.example.cristian.myapplication.ui.bovine.reproductive.ListZealFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReproductiveModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindListZealFragment(): ListZealFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindListServiceFragment():ListServiceFragment


}