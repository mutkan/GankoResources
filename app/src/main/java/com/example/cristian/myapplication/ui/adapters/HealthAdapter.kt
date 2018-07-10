package com.example.cristian.myapplication.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.menu.health.*
import javax.inject.Inject

class HealthAdapter @Inject constructor(val fragment: HealthFragment) : FragmentStatePagerAdapter(fragment.childFragmentManager) {
    override fun getItem(position: Int): Fragment = when(position){
        0 -> RecentHealthFragment.instance()
        1 -> NextHealthFragment.instance()
        else -> PendingHealthFragment.instance()
    }


    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence = when(position){
        0-> fragment.getString(R.string.recents)
        1-> fragment.getString(R.string.next)
        else -> fragment.getString(R.string.pendings)
    }

}