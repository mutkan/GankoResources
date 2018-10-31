package com.ceotic.ganko.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.ceotic.ganko.R
import com.ceotic.ganko.ui.menu.reports.ReportsFragment
import com.ceotic.ganko.ui.menu.reports.SelectAverageFragment
import com.ceotic.ganko.ui.menu.reports.SelectReportFragment
import javax.inject.Inject

class ReportsPagerAdapter @Inject constructor(val fragment: ReportsFragment): FragmentStatePagerAdapter(fragment.childFragmentManager){

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> SelectReportFragment.instance()
        else -> SelectAverageFragment.instance()
    }

    override fun getCount(): Int =  2

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 ->  fragment.getString(R.string.reports)
        else -> fragment.getString(R.string.averages)
    }

}