package com.example.cristian.myapplication.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.bovine.reproductive.ListServiceFragment
import com.example.cristian.myapplication.ui.bovine.reproductive.ListZealFragment
import com.example.cristian.myapplication.ui.bovine.reproductive.ReproductiveBvnActivity
import javax.inject.Inject

class ReproductiveBovineAdapter @Inject constructor(val activity: ReproductiveBvnActivity) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
    override fun getItem(position: Int): Fragment  = when(position){
        0 -> ListZealFragment.instance()
        else -> ListServiceFragment.instance()
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence = when(position){
        0-> activity.getString(R.string.zeal)
        else ->activity.getString(R.string.service)
    }

}