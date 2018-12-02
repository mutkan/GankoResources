package com.ceotic.ganko.ui.menu.bovine


import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListFeedBovineAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import kotlinx.android.synthetic.main.fragment_list_feed.*
import javax.inject.Inject

class ListFeedFragment : Fragment(), Injectable {

    @Inject
    lateinit var adapter: ListFeedBovineAdapter
    val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    companion object {
        fun instance():ListFeedFragment = ListFeedFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_feed, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerlistFeedBovines.adapter = adapter

    }


    override fun onResume() {
        super.onResume()
      /*  dis add viewModel.getAllFeed(idFinca)
                .subscribeBy(
                        onNext = {
                            isEmpty.set(it.isEmpty())
                            adapterRecent.feed = it
                        }
                )
    */}

}
