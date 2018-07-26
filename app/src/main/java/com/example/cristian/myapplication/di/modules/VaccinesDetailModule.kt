package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.groups.BovineSelectedFragment
import com.example.cristian.myapplication.ui.menu.vaccines.detail.ApplicationVaccineDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VaccinesDetailModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindApplicationsDetail(): ApplicationVaccineDetailFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBovineSelectedFragment(): BovineSelectedFragment


}