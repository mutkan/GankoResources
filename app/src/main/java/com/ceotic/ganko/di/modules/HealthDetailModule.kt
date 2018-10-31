package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.FragmentScope
import com.ceotic.ganko.ui.groups.BovineSelectedFragment
import com.ceotic.ganko.ui.menu.health.detail.ApplicationHealthDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HealthDetailModule{
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindApplicationHealthDetail(): ApplicationHealthDetailFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBovineSelectedHealthFragment(): BovineSelectedFragment

}