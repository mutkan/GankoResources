package com.example.cristian.myapplication.ui.menu.management


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ManageAdapter

import com.example.cristian.myapplication.util.LifeDisposable

import kotlinx.android.synthetic.main.fragment_manage.*

import javax.inject.Inject

class ManageFragment : Fragment(), Injectable{

    @Inject
    lateinit var adapter: ManageAdapter
    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pagerManage.adapter = adapter
        tabsManage.setupWithViewPager(pagerManage)
    }

    companion object {
        fun instance():ManageFragment = ManageFragment()
    }


}
