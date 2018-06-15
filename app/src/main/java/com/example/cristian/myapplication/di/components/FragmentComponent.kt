package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.FragmentScope

import com.example.cristian.myapplication.ui.feed.GroupsFeedFragment
import com.example.cristian.myapplication.ui.feed.SelectFeedFragment

import com.example.cristian.myapplication.ui.groups.SelectBovineFragment
import com.example.cristian.myapplication.ui.groups.SelectGroupFragment
import com.example.cristian.myapplication.ui.menu.straw.StrawFragment
import com.example.cristian.myapplication.ui.menu.bovine.ListBovineFragment
import com.example.cristian.myapplication.ui.menu.bovine.ListFeedFragment
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
import com.example.cristian.myapplication.ui.menu.meadow.MeadowFragment
import com.example.cristian.myapplication.ui.menu.meadow.aforo.AforoFragment
import com.example.cristian.myapplication.ui.menu.meadow.mantenimiento.MaintenanceFragment
import com.example.cristian.myapplication.ui.menu.meadow.size.SizeFragment
import com.example.cristian.myapplication.ui.menu.movement.MeadowUnusedFragment
import com.example.cristian.myapplication.ui.menu.movement.MeadowUsedFragment
import com.example.cristian.myapplication.ui.menu.movement.MovementFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentComponent{

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindListBovineFragment(): ListBovineFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindListManageFragment(): ManageFragment

    @FragmentScope
    @ContributesAndroidInjector()

    abstract fun  bindListFeedFragment(): ListFeedFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindSelectFeedFragment(): SelectFeedFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindGroupsFeedFragment(): GroupsFeedFragment

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




}