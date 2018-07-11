package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.groups.BovineSelectedFragment
import com.example.cristian.myapplication.ui.menu.vaccines.detail.ApplicationsDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VaccinesDetailModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindApplicationsDetail(): ApplicationsDetailFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBovineSelectedFragment(): BovineSelectedFragment


}