package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.SubFragmentScope
import com.example.cristian.myapplication.ui.menu.reports.SelectReportFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ReportsModule {

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindSelectReportFragment(): SelectReportFragment

}