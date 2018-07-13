package com.example.cristian.myapplication.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.groups.BovineSelectedFragment
import com.example.cristian.myapplication.ui.menu.management.detail.ApplicationManageDetailFragment
import com.example.cristian.myapplication.ui.menu.management.detail.ManageDetailActivity
import javax.inject.Inject

class ManageDetailPagerAdapter @Inject constructor(val activity: ManageDetailActivity): FragmentStatePagerAdapter(activity.supportFragmentManager) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> BovineSelectedFragment.instance(activity.idManage)
        else -> ApplicationManageDetailFragment.instance(activity.idDosisUno)
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> activity.getString(R.string.bovines)
        else -> activity.getString(R.string.aplications)
    }

}