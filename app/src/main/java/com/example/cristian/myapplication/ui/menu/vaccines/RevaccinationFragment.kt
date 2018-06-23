package com.example.cristian.myapplication.ui.menu.vaccines


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentRevaccinationBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.VaccineAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

class RevaccinationFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentRevaccinationBinding
    @Inject
    lateinit var adapter: VaccineAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val isEmpty: ObservableBoolean = ObservableBoolean(false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_revaccination, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        dis add adapter.clickRevaccination
//                .flatMap {
//
//                }
    }

//    private fun


    companion object {
        fun instance() = RevaccinationFragment()
    }


}
