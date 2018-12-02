package com.ceotic.ganko.di.modules

import com.ceotic.ganko.di.SubFragmentScope
import com.ceotic.ganko.ui.menu.management.NextManageFragment
import com.ceotic.ganko.ui.menu.management.PendingManageFragment
import com.ceotic.ganko.ui.menu.management.RecentManageFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ManageModule {

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindRecentManageFragment(): RecentManageFragment

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindNextManageFragment(): NextManageFragment

    @SubFragmentScope
    @ContributesAndroidInjector
    abstract fun bindPendingManageFragment(): PendingManageFragment

}