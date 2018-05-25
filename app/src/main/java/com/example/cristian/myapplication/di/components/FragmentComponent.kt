package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.feed.GroupsFeedFragment
import com.example.cristian.myapplication.ui.feed.SelectFeedFragment
import com.example.cristian.myapplication.ui.menu.bovine.ListBovineFragment
import com.example.cristian.myapplication.ui.menu.bovine.ListFeedFragment
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
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

}