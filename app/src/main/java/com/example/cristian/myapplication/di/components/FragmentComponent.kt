package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.menu.bovine.ListBovineFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentComponent{

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindListBovineFragment(): ListBovineFragment

}