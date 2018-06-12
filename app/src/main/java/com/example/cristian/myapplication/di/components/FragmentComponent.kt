package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.menu.Straw.StrawFragment
import com.example.cristian.myapplication.ui.menu.bovine.ListBovineFragment
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
import com.example.cristian.myapplication.ui.menu.meadow.MeadowFragment
import com.example.cristian.myapplication.ui.menu.meadow.aforo.AforoFragment
import com.example.cristian.myapplication.ui.menu.meadow.mantenimiento.MaintenanceFragment
import com.example.cristian.myapplication.ui.menu.meadow.size.SizeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentComponent{

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindListBovineFragment(): ListBovineFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindListManageFragment(): ManageFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindListStrawFragment(): StrawFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindMeadowFragment(): MeadowFragment

}