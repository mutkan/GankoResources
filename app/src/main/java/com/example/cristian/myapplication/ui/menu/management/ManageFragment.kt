package com.example.cristian.myapplication.ui.menu.management


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentApplicationManageDetailBinding
import com.example.cristian.myapplication.databinding.FragmentManageBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ManageAdapter

import com.example.cristian.myapplication.util.LifeDisposable
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector

import kotlinx.android.synthetic.main.fragment_manage.*

import javax.inject.Inject

class ManageFragment : Fragment(), HasSupportFragmentInjector{


    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentManageBinding
    @Inject
    lateinit var adapter: ManageAdapter

    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pagerManage.adapter = adapter
        tabsManage.setupWithViewPager(pagerManage)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    companion object {
        fun instance():ManageFragment = ManageFragment()
    }


}
