package com.example.cristian.myapplication.ui.menu.Straw


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.R.id.fabAddStrawFragment
import com.example.cristian.myapplication.R.id.recyclerStraw
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.StrawAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_manage.*
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

        dis add viewModel.getStraw(idFinca)
                .subscribeBy (
                        onSuccess = {
                            adapter.straw = it
                            if (it.isEmpty()) toast(R.string.empty_list)
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

