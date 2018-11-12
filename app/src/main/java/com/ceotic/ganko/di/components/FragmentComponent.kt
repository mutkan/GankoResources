package com.ceotic.ganko.di.components

import com.ceotic.ganko.di.FragmentScope
import com.ceotic.ganko.di.modules.ManageModule
import com.ceotic.ganko.di.modules.ReportsModule
import com.ceotic.ganko.di.modules.VaccinesModule

import com.ceotic.ganko.ui.groups.SelectGroupFragment
import com.ceotic.ganko.ui.menu.straw.StrawFragment
import com.ceotic.ganko.ui.menu.bovine.ListBovineFragment
import com.ceotic.ganko.ui.menu.bovine.ListFeedFragment
import com.ceotic.ganko.ui.menu.feed.FeedFragment
import com.ceotic.ganko.ui.menu.health.HealthFragment
import com.ceotic.ganko.ui.menu.health.RecentHealthFragment
import com.ceotic.ganko.ui.menu.health.NextHealthFragment
import com.ceotic.ganko.ui.menu.health.PendingHealthFragment
import com.ceotic.ganko.ui.menu.management.ManageFragment
import com.ceotic.ganko.ui.menu.meadow.MeadowFragment
import com.ceotic.ganko.ui.menu.milk.MilkFragment
import com.ceotic.ganko.ui.menu.movement.MeadowUnusedFragment
import com.ceotic.ganko.ui.menu.movement.MeadowUsedFragment
import com.ceotic.ganko.ui.menu.movement.MovementFragment
import com.ceotic.ganko.ui.menu.notifications.NotificationsFragment
import com.ceotic.ganko.ui.menu.reports.ReportsFragment
import com.ceotic.ganko.ui.menu.vaccines.VaccinesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentComponent{

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindListBovineFragment(): ListBovineFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun  bindListFeedFragment(): ListFeedFragment


    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindListStrawFragment(): StrawFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindMeadowFragment(): MeadowFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindMeadowUnusedFragment(): MeadowUnusedFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindMeadowUsedFragment(): MeadowUsedFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindMovementFragment(): MovementFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindSelectGroupFragment(): SelectGroupFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindMilkFragment(): MilkFragment



    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindRecentHealthFragment(): RecentHealthFragment


    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindHealthFragment(): HealthFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindNextHealthFragment(): NextHealthFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindPendingHealthFragment(): PendingHealthFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindFeedFragment(): FeedFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [(VaccinesModule::class)])
    abstract fun bindVaccinesFragment(): VaccinesFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [(ManageModule::class)])
    abstract fun bindManageFragment(): ManageFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [(ReportsModule::class)])
    abstract fun bindReportsFragment(): ReportsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindNotificationsFragment(): NotificationsFragment




}