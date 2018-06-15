package com.example.cristian.myapplication.ui.groups.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.cristian.myapplication.ui.groups.SelectBovineFragment
import com.example.cristian.myapplication.ui.groups.SelectGroupFragment

class SelectPagerAdapter(fm:FragmentManager): FragmentPagerAdapter(fm){

    var titles:Array<String> = emptyArray()

    override fun getItem(position: Int): Fragment = when(position){
        0 -> SelectBovineFragment.instance(false)
        else -> SelectGroupFragment.instance(false)
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence?  = titles[position]

}