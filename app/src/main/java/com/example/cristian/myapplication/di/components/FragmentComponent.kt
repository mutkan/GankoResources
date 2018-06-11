package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.menu.straw.StrawFragment
import com.example.cristian.myapplication.ui.menu.bovine.ListBovineFragment
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
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

}