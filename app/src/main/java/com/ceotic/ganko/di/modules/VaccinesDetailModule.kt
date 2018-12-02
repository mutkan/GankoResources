package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.FragmentScope
import com.ceotic.ganko.ui.groups.BovineSelectedFragment
import com.ceotic.ganko.ui.menu.vaccines.detail.ApplicationVaccineDetailFragment
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