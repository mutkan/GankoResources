package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.menu.meadow.aforo.AforoFragment
import com.example.cristian.myapplication.ui.menu.meadow.mantenimiento.MaintenanceFragment
import com.example.cristian.myapplication.ui.menu.meadow.size.SizeFragment
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