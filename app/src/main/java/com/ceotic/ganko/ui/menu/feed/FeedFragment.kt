package com.ceotic.ganko.ui.menu.feed


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListFeedBovineAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import dagger.android.DispatchingAndroidInjector
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_list_feed.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class FeedFragment : Fragment(), Injectable {
    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
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
        adapter.notifyDataSetChanged()
        recyclerlistFeedBovines.adapter = adapter
        recyclerlistFeedBovines.layoutManager = LinearLayoutManager(activity)
        dis add viewModel.getFeed()
                .subscribeBy (
                    onNext= {
                        adapter.feed = it
                        if (it.isEmpty()) emptyListFeed.visibility = View.VISIBLE
                        else emptyListFeed.visibility = View.GONE
                    },
                    onError = {
                        toast(it.message!!)}
                )
        dis add fabfeedlistFragment.clicks()
                .subscribe {
                    startActivity<AddFeedActivity>()
                }
        }
    companion object {
        fun instance(): FeedFragment = FeedFragment()
    }


}
