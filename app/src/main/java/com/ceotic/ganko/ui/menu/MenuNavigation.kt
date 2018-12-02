package com.ceotic.ganko.ui.menu

import com.ceotic.ganko.R
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.di.ActivityScope
import com.ceotic.ganko.ui.account.LoginActivity
import com.ceotic.ganko.ui.farms.FarmActivity
import com.ceotic.ganko.ui.groups.SelectGroupFragment
import com.ceotic.ganko.ui.menu.straw.StrawFragment
import com.ceotic.ganko.ui.menu.bovine.ListBovineFragment

import com.ceotic.ganko.ui.menu.feed.FeedFragment
import com.ceotic.ganko.ui.menu.health.HealthFragment
import com.ceotic.ganko.ui.menu.management.ManageFragment
import com.ceotic.ganko.ui.menu.meadow.MeadowFragment
import com.ceotic.ganko.ui.menu.milk.MilkFragment
import com.ceotic.ganko.ui.menu.movement.MovementFragment
import com.ceotic.ganko.ui.menu.notifications.NotificationsFragment
import com.ceotic.ganko.ui.menu.reports.ReportsFragment
import com.ceotic.ganko.ui.menu.vaccines.VaccinesFragment
import com.ceotic.ganko.util.putFragment
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

    fun navigateToGroups() {
        activity.putFragment(R.id.content_frame, SelectGroupFragment.instance(true))
    }

    fun navigateToMilk() {
        activity.putFragment(R.id.content_frame, MilkFragment.instance())
    }

    fun navigateToFeeding() {
        activity.putFragment(R.id.content_frame,FeedFragment.instance())
    }

    fun navigateToHealth() {
        activity.putFragment(R.id.content_frame, HealthFragment.instance())
    }

    fun navigateToStraw() {
        activity.putFragment(R.id.content_frame,StrawFragment.instance())
    }

    fun navigateToManage() {
        activity.putFragment(R.id.content_frame,ManageFragment.instance())
    }

    fun navigateToMovements() {
        activity.putFragment(R.id.content_frame,MovementFragment.instance())
    }

    fun navigateToMeadow() {
        activity.putFragment(R.id.content_frame, MeadowFragment.instance())
    }

    fun navigateToVaccination() {
        activity.putFragment(R.id.content_frame,VaccinesFragment.instance())
    }

    fun navigateToNotification() {
        activity.putFragment(R.id.content_frame,NotificationsFragment.instance())

    }

    fun navigateToLogout() {
        sesion.destroysession()

        activity.startActivity<LoginActivity>()
        activity.finish()
    }

    fun navigateToDetail() {

    }

    fun navigateToReports() {
//        activity.putFragment(R.id.content_frame, ReportsFragment.instance())
    }

}


