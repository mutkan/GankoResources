package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.FragmentScope
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment
import com.ceotic.ganko.ui.bovine.reproductive.ListZealFragment
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