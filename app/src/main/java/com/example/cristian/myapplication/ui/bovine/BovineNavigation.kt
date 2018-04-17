package com.example.cristian.myapplication.ui.bovine

import com.example.cristian.myapplication.di.ActivityScope
import com.example.cristian.myapplication.ui.bovine.ceba.CebaBvnActivity
import com.example.cristian.myapplication.ui.bovine.feed.FeedBvnActivity
import com.example.cristian.myapplication.ui.bovine.health.HealthBvnActivity
import com.example.cristian.myapplication.ui.bovine.manage.ManageBvnActivity
import com.example.cristian.myapplication.ui.bovine.milk.MilkBvnActivity
import com.example.cristian.myapplication.ui.bovine.movement.MovementBvnActivity
import com.example.cristian.myapplication.ui.bovine.vaccination.VaccinationBvnActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject

@ActivityScope
class BovineNavigation @Inject constructor(val activity: DetailBovineActivity){

    fun navigateToFeedBvnActivity(){
        activity.startActivity<FeedBvnActivity>()
    }

    fun navigateToManageBvnActivity(){
        activity.startActivity<ManageBvnActivity>()
    }

    fun navigateToHealthBvnActivity(){
        activity.startActivity<HealthBvnActivity>()
    }

    fun navigateToMilkBvnActivity(){
        activity.startActivity<MilkBvnActivity>()
    }

    fun navigateToVaccinationBvnActivity(){
        activity.startActivity<VaccinationBvnActivity>()
    }

    fun navigateToMovementsBvnActivity(){
        activity.startActivity<MovementBvnActivity>()
    }

    fun navigateToCebaBvnActivity(){
        activity.startActivity<CebaBvnActivity>()
    }
}