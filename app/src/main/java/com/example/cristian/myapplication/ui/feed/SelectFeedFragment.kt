package com.example.cristian.myapplication.ui.feed


import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListFeedBovineAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SelectFeedFragment : Fragment(), Injectable {

    @Inject
    lateinit var adapter: ListFeedBovineAdapter
    val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: FeedViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    companion object {
        fun newInstance(): SelectFeedFragment{
            val fragment = SelectFeedFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_feed, container, false)
    }





}
