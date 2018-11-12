package com.ceotic.ganko.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.ceotic.ganko.R
import com.ceotic.ganko.ui.groups.BovineSelectedFragment
import com.ceotic.ganko.ui.menu.health.detail.ApplicationHealthDetailFragment
import com.ceotic.ganko.ui.menu.health.detail.HealthDetailActivity
import javax.inject.Inject

class HealthDetailPagerAdapter  @Inject constructor(val activity :HealthDetailActivity): FragmentStatePagerAdapter(activity.supportFragmentManager){
    override fun getItem(position: Int): Fragment = when(position) {
        0 -> BovineSelectedFragment.instance(activity.idHealth)
        else -> ApplicationHealthDetailFragment.instance(activity.idAplicacionUno)
    }
    override fun getCount(): Int =2

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> activity.getString(R.string.bovines)
        else -> activity.getString(R.string.aplications)
    }


}