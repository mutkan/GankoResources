package com.ceotic.ganko.ui.menu.reports


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.FragmentReportsBinding
import com.ceotic.ganko.ui.adapters.ReportsPagerAdapter
import com.ceotic.ganko.util.LifeDisposable
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_reports.*
import javax.inject.Inject

class ReportsFragment : Fragment(), HasSupportFragmentInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentReportsBinding
    @Inject
    lateinit var adapter: ReportsPagerAdapter

    val dis: LifeDisposable = LifeDisposable(this)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reports, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pagerReports.adapter = adapter
        tabsReports.setupWithViewPager(pagerReports)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    companion object {
        fun instance(): ReportsFragment = ReportsFragment()
    }



}
