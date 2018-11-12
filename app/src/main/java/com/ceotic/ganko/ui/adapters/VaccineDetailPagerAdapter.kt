package com.ceotic.ganko.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.ceotic.ganko.R
import com.ceotic.ganko.ui.groups.BovineSelectedFragment
import com.ceotic.ganko.ui.menu.vaccines.detail.ApplicationVaccineDetailFragment
import com.ceotic.ganko.ui.menu.vaccines.detail.VaccineDetailActivity
import javax.inject.Inject

class VaccineDetailPagerAdapter @Inject constructor(val activity: VaccineDetailActivity) : FragmentStatePagerAdapter(activity.supportFragmentManager) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> BovineSelectedFragment.instance(activity.idVaccine)
        else -> ApplicationVaccineDetailFragment.instance(activity.idDosisUno)
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> activity.getString(R.string.bovines)
        else -> activity.getString(R.string.aplications)
    }

}