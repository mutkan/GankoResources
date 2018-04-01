package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.ActivityScope
import com.example.cristian.myapplication.ui.bovine.milk.AddMilkBvnActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityComponents{

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddMilkBvnActivity(): AddMilkBvnActivity

}