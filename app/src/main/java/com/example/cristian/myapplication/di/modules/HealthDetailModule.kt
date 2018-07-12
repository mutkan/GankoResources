package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.groups.BovineSelectedFragment
import com.example.cristian.myapplication.ui.menu.health.detail.ApplicationHealthDetailFragment
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