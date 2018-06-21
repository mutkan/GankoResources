package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.SubFragmentScope
import com.example.cristian.myapplication.ui.menu.vaccines.RevaccinationFragment
import com.example.cristian.myapplication.ui.menu.vaccines.VaccinationsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VaccinesModule {

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindVaccinationsFragment(): VaccinationsFragment

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindRevaccinationFragment(): RevaccinationFragment
}