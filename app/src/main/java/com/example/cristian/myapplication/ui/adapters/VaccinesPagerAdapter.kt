package com.example.cristian.myapplication.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.menu.vaccines.RevaccinationFragment
import com.example.cristian.myapplication.ui.menu.vaccines.VaccinationsFragment
import com.example.cristian.myapplication.ui.menu.vaccines.VaccinesFragment
import javax.inject.Inject

class VaccinesPagerAdapter @Inject constructor(val fragment:VaccinesFragment): FragmentStatePagerAdapter(fragment.childFragmentManager) {

    override fun getItem(position: Int): Fragment = when(position){
        0 -> VaccinationsFragment.instance()
        else -> RevaccinationFragment.instance()
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence = when(position){
        0-> fragment.getString(R.string.recents)
        else ->fragment.getString(R.string.next)
    }

}