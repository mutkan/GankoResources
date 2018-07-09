package com.example.cristian.myapplication.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
import com.example.cristian.myapplication.ui.menu.management.NextManageFragment
import com.example.cristian.myapplication.ui.menu.management.PendingManageFragment
import com.example.cristian.myapplication.ui.menu.management.RecentManageFragment
import javax.inject.Inject

class ManageAdapter @Inject constructor(val fragment: ManageFragment) : FragmentStatePagerAdapter(fragment.childFragmentManager) {

    override fun getItem(position: Int): Fragment = when(position) {
        0 -> RecentManageFragment.instance()
        1 -> NextManageFragment.instance()
        else -> PendingManageFragment.instance()
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence = when(position){
        0-> fragment.getString(R.string.recents)
        1 ->fragment.getString(R.string.next)
        else -> fragment.getString(R.string.pending)
    }

}