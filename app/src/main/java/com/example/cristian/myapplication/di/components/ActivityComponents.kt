package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.ActivityScope
import com.example.cristian.myapplication.ui.bovine.feed.FeedBvnActivity
import com.example.cristian.myapplication.ui.bovine.health.HealthBvnActivity
import com.example.cristian.myapplication.ui.bovine.manage.ManageBvnActivity
import com.example.cristian.myapplication.ui.bovine.milk.AddMilkBvnActivity
import com.example.cristian.myapplication.ui.bovine.milk.MilkBvnActivity
import com.example.cristian.myapplication.ui.farms.AddFarmActivity
import com.example.cristian.myapplication.ui.farms.FarmActivity
import com.example.cristian.myapplication.ui.menu.MenuActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityComponents{

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMilkBvnActivity(): MilkBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddMilkBvnActivity(): AddMilkBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindFeedBvnActivity(): FeedBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindHealthBvnActivity(): HealthBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindManageBvnActivity(): ManageBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMenuActivity(): MenuActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindFarmActivity(): FarmActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddFarmActivity(): AddFarmActivity

}