package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.FragmentScope
import com.ceotic.ganko.ui.groups.SelectBovineFragment
import com.ceotic.ganko.ui.groups.SelectGroupFragment
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