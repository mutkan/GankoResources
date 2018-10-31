package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.FragmentScope
import com.ceotic.ganko.ui.menu.meadow.aforo.AforoFragment
import com.ceotic.ganko.ui.menu.meadow.mantenimiento.MaintenanceFragment
import com.ceotic.ganko.ui.menu.meadow.size.SizeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MeadowModule{
    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindAforoFragment(): AforoFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindMaintenanceFragment(): MaintenanceFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindSizeFragment(): SizeFragment
}