package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.SubFragmentScope
import com.example.cristian.myapplication.ui.menu.vaccines.NextVaccinesFragment
import com.example.cristian.myapplication.ui.menu.vaccines.RecentVaccinesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VaccinesModule {

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindVaccinationsFragment(): RecentVaccinesFragment

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindRevaccinationFragment(): NextVaccinesFragment
}