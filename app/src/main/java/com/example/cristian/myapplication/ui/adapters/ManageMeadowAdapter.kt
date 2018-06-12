package com.example.cristian.myapplication.ui.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.ui.menu.meadow.aforo.AforoFragment
import com.example.cristian.myapplication.ui.menu.meadow.mantenimiento.MaintenanceFragment
import com.example.cristian.myapplication.ui.menu.meadow.size.SizeFragment


class ManageMeadowAdapter(val context: Context, fragmentManger: FragmentManager,val meadow:Pradera)
    : FragmentStatePagerAdapter(fragmentManger){

    var totalTabs = 3

    override fun getItem(position: Int): Fragment =
            when(position){
                0 -> SizeFragment.instance(meadow)
                1 -> MaintenanceFragment.instance(meadow)
                else -> AforoFragment.instance(meadow)
            }

    override fun getCount(): Int = totalTabs

    override fun getPageTitle(position: Int): CharSequence? =
            when(position){
                0 -> "Tamaño y Graminea"
                1 -> "Mantenimientos"
                else -> "Aforos"

            }

}