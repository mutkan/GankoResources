package com.example.cristian.myapplication.ui.groups


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R

class SelectBovineFragment : Fragment() {

    val createGroup:Boolean by lazy { arguments?.getBoolean(ARG_EDITABLE) ?: false }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_bovine, container, false)
    }

    companion object {

        private const val ARG_EDITABLE = "edtiable"

        @JvmStatic
        fun instance(createGroup:Boolean):SelectBovineFragment = SelectBovineFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_EDITABLE, createGroup)
            }
        }
    }


}
