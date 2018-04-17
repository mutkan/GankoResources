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

    fun navigateToFeedBvnActivity(id: String){
        activity.startActivity<FeedBvnActivity>("idBovine" to id)
    }

    fun navigateToManageBvnActivity(id: String){
        activity.startActivity<ManageBvnActivity>("idBovine" to id)
    }

    fun navigateToHealthBvnActivity(id: String){
        activity.startActivity<HealthBvnActivity>("idBovine" to id)
    }

    fun navigateToMilkBvnActivity(id: String){
        activity.startActivity<MilkBvnActivity>("idBovine" to id)
    }

    fun navigateToVaccinationBvnActivity(id: String){
        activity.startActivity<VaccinationBvnActivity>("idBovine" to id)
    }

    fun navigateToMovementsBvnActivity(id: String){
        activity.startActivity<MovementBvnActivity>("idBovine" to id)
    }

    fun navigateToCebaBvnActivity(id: String){
        activity.startActivity<CebaBvnActivity>("idBovine" to id)
    }

    fun navigateToReproductiveBvnActivity(id: String){
        activity.startActivity<CebaBvnActivity>("idBovine" to id)
    }
}