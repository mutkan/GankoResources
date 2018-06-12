package com.example.cristian.myapplication.ui.menu

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.di.ActivityScope
import com.example.cristian.myapplication.ui.account.LoginActivity
import com.example.cristian.myapplication.ui.farms.FarmActivity
import com.example.cristian.myapplication.ui.menu.straw.StrawFragment
import com.example.cristian.myapplication.ui.menu.bovine.ListBovineFragment
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
import com.example.cristian.myapplication.ui.menu.meadow.MeadowFragment
import com.example.cristian.myapplication.util.putFragment
import org.jetbrains.anko.startActivity
import javax.inject.Inject

/**
 * Created by Ana Marin on 11/03/2018.
 */
@ActivityScope
class MenuNavigation @Inject constructor(val activity: MenuActivity, val sesion: UserSession){

    fun navigateToFarm() {
        sesion.farm = ""
        activity.startActivity<FarmActivity>()
        activity.finish()
    }

    fun navigateToBovines() {
        activity.putFragment(R.id.content_frame,ListBovineFragment.instance())
    }

    fun navigateToMilk() {
    }

    fun navigateToFeeding() {

    }

    fun navigateToHealth() {

    }

    fun navigateToStraw() {
        activity.putFragment(R.id.content_frame,StrawFragment.instance())
    }

    fun navigateToManage() {
        activity.putFragment(R.id.content_frame,ManageFragment.instance())
    }

    fun navigateToMovements() {

    }

    fun navigateToMeadow() {
        activity.putFragment(R.id.content_frame, MeadowFragment.instance())
    }

    fun navigateToVaccination() {

    }

    fun navigateToLogout() {
        sesion.destroysession()

        activity.startActivity<LoginActivity>()
        activity.finish()
    }

    fun navigateToDetail() {

    }

    fun navigateToReports() {

    }

}


