package com.example.cristian.myapplication.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
import com.example.cristian.myapplication.ui.menu.management.NextManageFragment
import com.example.cristian.myapplication.ui.menu.management.RecentManageFragment
import javax.inject.Inject

class ManageAdapter @Inject constructor(val fragment: ManageFragment) : FragmentStatePagerAdapter(fragment.childFragmentManager) {

    override fun getItem(position: Int): Fragment = when(position) {
        0 -> RecentManageFragment.instance()
        else -> NextManageFragment.instance()
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence = when(position){
        0-> fragment.getString(R.string.recents)
        else ->fragment.getString(R.string.next)
    }

}