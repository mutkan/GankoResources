package com.example.cristian.myapplication.ui.menu.feed


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.StrawAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

class FeedFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    @Inject
    lateinit var adapter: StrawAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }


}
