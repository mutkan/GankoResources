package com.example.cristian.myapplication.ui.bovine.reproductive

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentListServiceBinding

class ListServiceFragment : Fragment() {

    lateinit var binding: FragmentListServiceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_service, container, false)
        return binding.root
    }

    companion object {
        fun instance():ListServiceFragment = ListServiceFragment()
    }
}