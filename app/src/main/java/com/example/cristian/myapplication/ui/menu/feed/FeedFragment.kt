package com.example.cristian.myapplication.ui.menu.feed


import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListFeedBovineAdapter
import com.example.cristian.myapplication.ui.adapters.StrawAdapter
import com.example.cristian.myapplication.ui.feed.AddFeedActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.ui.menu.straw.StrawAddActivity
import com.example.cristian.myapplication.ui.menu.straw.StrawFragment
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_list_feed.*
import kotlinx.android.synthetic.main.fragment_straw.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class FeedFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    @Inject
    lateinit var adapter: ListFeedBovineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_feed, container, false)
    }

    override fun onResume() {
        super.onResume()
        recyclerlistFeedBovines.adapter = adapter
        recyclerlistFeedBovines.layoutManager = LinearLayoutManager(activity)
        dis add viewModel.getFeed(idFinca)
                .subscribeBy (
                    onSuccess = {
                        adapter.feed = it
                        if (it.isEmpty()) toast(R.string.empty_list)
                    },
                    onError = {
                        toast(it.message!!)}
                )
        dis add fabAddStrawFragment.clicks()
                .subscribe {
                    startActivity<AddFeedActivity>()
                }
        }
    companion object {
        fun instance(): FeedFragment = FeedFragment()
    }


}
