package com.example.cristian.myapplication.ui.menu.reports


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R


class SelectReportFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_report, container, false)
    }

    companion object {
        fun instance(): SelectReportFragment = SelectReportFragment()
    }


}
