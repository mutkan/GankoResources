package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.FragmentScope
import com.ceotic.ganko.ui.groups.BovineSelectedFragment
import com.ceotic.ganko.ui.menu.management.detail.ApplicationManageDetailFragment
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