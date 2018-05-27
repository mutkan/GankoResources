package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.FragmentScope
<<<<<<< HEAD
import com.example.cristian.myapplication.ui.feed.GroupsFeedFragment
import com.example.cristian.myapplication.ui.feed.SelectFeedFragment
=======
import com.example.cristian.myapplication.ui.menu.Straw.StrawFragment
>>>>>>> e6a54db3461172cdd43998523242f505b7ba1999
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
<<<<<<< HEAD
    abstract fun  bindListFeedFragment(): ListFeedFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindSelectFeedFragment(): SelectFeedFragment

    @FragmentScope
    @ContributesAndroidInjector()
    abstract fun bindGroupsFeedFragment(): GroupsFeedFragment
=======
    abstract fun bindListStrawFragment(): StrawFragment

>>>>>>> e6a54db3461172cdd43998523242f505b7ba1999
}