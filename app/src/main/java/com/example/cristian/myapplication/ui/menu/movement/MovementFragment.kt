package com.example.cristian.myapplication.ui.menu.movement


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentMovementBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ManageMovementsAdapter
import kotlinx.android.synthetic.main.fragment_movement.*

class MovementFragment : Fragment(), Injectable {

    lateinit var binding: FragmentMovementBinding
    val adapter: ManageMovementsAdapter by lazy { ManageMovementsAdapter(context!!, activity!!.supportFragmentManager) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movement, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        movementTab.setupWithViewPager(movementPager)
        movementPager.adapter = adapter
    }


    companion object {
        fun instance(): MovementFragment = MovementFragment()
    }
}
