package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.SubFragmentScope
import com.ceotic.ganko.ui.menu.vaccines.NextVaccinesFragment
import com.ceotic.ganko.ui.menu.vaccines.RecentVaccinesFragment
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