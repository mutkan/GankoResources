package com.example.cristian.myapplication.di.modules

import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.ui.feed.GroupsFeedFragment
import com.example.cristian.myapplication.ui.feed.SelectFeedFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

@Module
abstract class FeedModule {
    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindSelectFeedFragment(): SelectFeedFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindGroupsFeedFragment(): GroupsFeedFragment
}