package com.ceotic.ganko.ui.menu.milk


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.FragmentListMilkBinding

class ListMilkFragment : Fragment() {

    lateinit var binding: FragmentListMilkBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_milk, container, false)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        binding.isEmpty = false
    }

    companion object {
        fun instance(): ListMilkFragment = ListMilkFragment()
    }


}
