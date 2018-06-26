package com.example.cristian.myapplication.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.menu.health.HealthFragment
import com.example.cristian.myapplication.ui.menu.health.NextHealthFragment
import com.example.cristian.myapplication.ui.menu.health.RecentHealthFragment
import javax.inject.Inject

class HealthAdapter @Inject constructor(val fragment: HealthFragment) : FragmentStatePagerAdapter(fragment.childFragmentManager) {
    override fun getItem(position: Int): Fragment = when(position){
        0 -> RecentHealthFragment.instance()
        else -> NextHealthFragment.instance()
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence = when(position){
        0-> fragment.getString(R.string.recents)
        else ->fragment.getString(R.string.next)
    }

}