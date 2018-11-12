package com.ceotic.ganko.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.ceotic.ganko.R
import com.ceotic.ganko.ui.menu.management.ManageFragment
import com.ceotic.ganko.ui.menu.management.NextManageFragment
import com.ceotic.ganko.ui.menu.management.PendingManageFragment
import com.ceotic.ganko.ui.menu.management.RecentManageFragment
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