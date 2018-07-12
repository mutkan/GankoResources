package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.groups.BovineSelectedFragment
import com.example.cristian.myapplication.ui.menu.management.detail.ApplicationManageDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ManageDetailModule{

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindApplicationManageDetailFragment(): ApplicationManageDetailFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBovineSelectedFragment(): BovineSelectedFragment
}