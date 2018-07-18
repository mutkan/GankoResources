package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.SubFragmentScope
import com.example.cristian.myapplication.ui.menu.management.NextManageFragment
import com.example.cristian.myapplication.ui.menu.management.PendingManageFragment
import com.example.cristian.myapplication.ui.menu.management.RecentManageFragment
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