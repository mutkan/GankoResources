package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.FragmentScope
import com.ceotic.ganko.ui.groups.SelectBovineFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AddGroupModule{

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSelectBovineFragment(): SelectBovineFragment

}