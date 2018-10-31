package com.ceotic.ganko.ui.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.ceotic.ganko.ui.menu.movement.MeadowUnusedFragment
import com.ceotic.ganko.ui.menu.movement.MeadowUsedFragment

class ManageMovementsAdapter(val context: Context, fragmentManger: FragmentManager)
    : FragmentStatePagerAdapter(fragmentManger){

    var totalTabs = 2

    override fun getItem(position: Int): Fragment =
            when(position){
                0 -> MeadowUnusedFragment.instance()
                else -> MeadowUsedFragment.instance()
            }

    override fun getCount(): Int = totalTabs

    override fun getPageTitle(position: Int): CharSequence? =
            when(position){
                0 -> "Praderas Libres"
                else -> "Praderas Ocupadas"

            }

}