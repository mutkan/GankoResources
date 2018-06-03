package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.group.GroupsFragment
import com.example.cristian.myapplication.ui.group.SelectFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FeedModule {
    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindSelectFeedFragment(): SelectFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindGroupsFeedFragment(): GroupsFragment
}