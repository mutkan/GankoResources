package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.groups.SelectBovineFragment
import com.example.cristian.myapplication.ui.groups.SelectGroupFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SelectModule{

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSelectGroupFragment():SelectGroupFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSelectBovineFragment():SelectBovineFragment


}