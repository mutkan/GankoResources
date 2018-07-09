package com.example.cristian.myapplication.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.menu.vaccines.NextVaccinesFragment
import com.example.cristian.myapplication.ui.menu.vaccines.NextVaccinesFragment.Companion.TYPE_NEXT
import com.example.cristian.myapplication.ui.menu.vaccines.NextVaccinesFragment.Companion.TYPE_PENDING
import com.example.cristian.myapplication.ui.menu.vaccines.RecentVaccinesFragment
import com.example.cristian.myapplication.ui.menu.vaccines.VaccinesFragment
import javax.inject.Inject

class VaccinesPagerAdapter @Inject constructor(val fragment:VaccinesFragment): FragmentStatePagerAdapter(fragment.childFragmentManager) {

    override fun getItem(position: Int): Fragment = when(position){
        0 -> RecentVaccinesFragment.instance()
        1 -> NextVaccinesFragment.instance(TYPE_NEXT)
        else ->NextVaccinesFragment.instance(TYPE_PENDING)
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence = when(position){
        0-> fragment.getString(R.string.recents)
        1 ->fragment.getString(R.string.next)
        else ->fragment.getString(R.string.pending)
    }

}