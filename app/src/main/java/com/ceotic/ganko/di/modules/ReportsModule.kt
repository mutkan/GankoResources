package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.SubFragmentScope
import com.ceotic.ganko.ui.menu.reports.SelectAverageFragment
import com.ceotic.ganko.ui.menu.reports.SelectReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReportsModule {

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindSelectReportFragment(): SelectReportFragment

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindSelectAverageFragment(): SelectAverageFragment


}