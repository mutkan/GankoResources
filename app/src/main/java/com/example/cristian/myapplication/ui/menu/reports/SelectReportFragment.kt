package com.example.cristian.myapplication.ui.menu.reports


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentSelectReportBinding


class SelectReportFragment : Fragment() {

    lateinit var binding: FragmentSelectReportBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_report, container, false)
        return binding.root
    }

    companion object {
        fun instance(): SelectReportFragment = SelectReportFragment()
    }


}
