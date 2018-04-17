package com.example.cristian.myapplication.ui.menu

import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.di.ActivityScope
import com.example.cristian.myapplication.ui.account.LoginActivity
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * Created by Ana Marin on 11/03/2018.
 */
@ActivityScope
class MenuNavigation @Inject constructor(val activity: MenuActivity, val sesion: UserSession){

    fun navigateToFarm() {

    }

    fun navigateToBovines() {

    }

    fun navigateToFeeding() {

    }

    fun navigateToHealth() {

    }

    fun navigateToManage() {

    }

    fun navigateToMovements() {

    }

    fun navigateToPrairies() {

    }

    fun navigateToVaccination() {

    }

    fun navigateToLogout() {
        sesion.logged = false
        sesion.token = ""

        activity.startActivity<LoginActivity>()
        activity.finish()
    }

    fun navigateToDetail() {

    }

    fun navigateToReports() {

    }

}


