package com.ceotic.ganko.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.ceotic.ganko.R
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.TYPE_ON_SERVICE
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.TYPE_SERVICES
import com.ceotic.ganko.ui.bovine.reproductive.ListZealFragment
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnActivity
import javax.inject.Inject

class ReproductiveBovineAdapter @Inject constructor(val activity: ReproductiveBvnActivity) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
    override fun getItem(position: Int): Fragment  = when(position){
        0 -> ListZealFragment.instance(activity.idBovino)
        1 -> ListServiceFragment.instance(activity.idBovino, TYPE_ON_SERVICE)
        else -> ListServiceFragment.instance(activity.idBovino, TYPE_SERVICES)
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence = when(position){
        0-> activity.getString(R.string.zeal)
        1 -> activity.getString(R.string.onService)
        else ->activity.getString(R.string.services)
    }

}