package com.ceotic.ganko.ui.menu.straw


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.StrawAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_straw.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


class StrawFragment :Fragment(), Injectable {

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
        return inflater.inflate(R.layout.fragment_straw, container, false)
    }

    override fun onResume() {
        super.onResume()
        recyclerStraw.adapter = adapter
        recyclerStraw.layoutManager = LinearLayoutManager(activity)

        dis add viewModel.getStraw(idFinca)
                .subscribeBy (
                        onNext = {
                            adapter.straw = it
                            if (it.isEmpty()) emptyListStraw.visibility = View.VISIBLE
                            else emptyListStraw.visibility = View.GONE
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add fabAddStrawFragment.clicks()
                .subscribe {
                    startActivity<StrawAddActivity>()
                }

    }

    companion object {
        fun instance(): StrawFragment = StrawFragment()
    }
}

