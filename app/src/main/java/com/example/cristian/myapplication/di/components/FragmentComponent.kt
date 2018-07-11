package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.di.modules.VaccinesModule

import com.example.cristian.myapplication.ui.groups.SelectGroupFragment
import com.example.cristian.myapplication.ui.menu.straw.StrawFragment
import com.example.cristian.myapplication.ui.menu.bovine.ListBovineFragment
import com.example.cristian.myapplication.ui.menu.bovine.ListFeedFragment
import com.example.cristian.myapplication.ui.menu.feed.FeedFragment
import com.example.cristian.myapplication.ui.menu.health.HealthFragment
import com.example.cristian.myapplication.ui.menu.health.RecentHealthFragment
import com.example.cristian.myapplication.ui.menu.health.NextHealthFragment
import com.example.cristian.myapplication.ui.menu.health.PendingHealthFragment
import com.example.cristian.myapplication.ui.menu.health.detail.ApplicationHealthDetailFragment
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
import com.example.cristian.myapplication.ui.menu.management.NextManageFragment
import com.example.cristian.myapplication.ui.menu.management.RecentManageFragment
import com.example.cristian.myapplication.ui.menu.meadow.MeadowFragment
import com.example.cristian.myapplication.ui.menu.milk.MilkFragment
import com.example.cristian.myapplication.ui.menu.movement.MeadowUnusedFragment
import com.example.cristian.myapplication.ui.menu.movement.MeadowUsedFragment
import com.example.cristian.myapplication.ui.menu.movement.MovementFragment
import com.example.cristian.myapplication.ui.menu.vaccines.VaccinesFragment
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
    @ContributesAndroidInjector()
    abstract fun bindManageFragment(): ManageFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindRecentManageFragment(): RecentManageFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindNextManageFragment(): NextManageFragment






}