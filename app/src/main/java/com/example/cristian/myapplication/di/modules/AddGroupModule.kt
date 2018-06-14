package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.groups.SelectBovineFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AddGroupModule{

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSelectBovineFragment(): SelectBovineFragment

}