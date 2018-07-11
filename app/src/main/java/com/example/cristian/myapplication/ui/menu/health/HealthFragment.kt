package com.example.cristian.myapplication.ui.menu.health


import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable

import com.example.cristian.myapplication.ui.adapters.HealthAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
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
    }


    companion object {
        fun instance(): HealthFragment = HealthFragment()
    }


}
