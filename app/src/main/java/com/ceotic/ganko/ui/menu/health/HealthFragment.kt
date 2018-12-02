package com.ceotic.ganko.ui.menu.health


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.HealthAdapter
import com.ceotic.ganko.ui.common.PageChangeListener
import com.ceotic.ganko.util.LifeDisposable
import kotlinx.android.synthetic.main.fragment_health.*
import javax.inject.Inject


class HealthFragment : Fragment() , Injectable {

    @Inject
    lateinit var adapter: HealthAdapter
    val dis: LifeDisposable = LifeDisposable(this)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       return inflater.inflate(R.layout.fragment_health, container, false)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pagerHealth.adapter = adapter
        tabsHealth.setupWithViewPager(pagerHealth)
        pagerHealth.addOnPageChangeListener(PageChangeListener)
    }


    companion object {
        fun instance(): HealthFragment = HealthFragment()
    }


}
