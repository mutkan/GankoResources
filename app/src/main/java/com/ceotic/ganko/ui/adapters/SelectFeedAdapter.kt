package com.ceotic.ganko.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class SelectFeedAdapter (manager : FragmentManager) : FragmentPagerAdapter(manager) {
    val fragments = ArrayList<Fragment>()
    val titles = ArrayList<String>()

    override fun getItem(position: Int): Fragment = fragments.get(position)


    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence? = titles.get(position)

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)
    }
}
