package com.ceotic.ganko.ui.menu.management


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.FragmentRecentManageBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.RecentManageAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.ui.menu.management.detail.ManageDetailActivity
import com.ceotic.ganko.ui.menu.management.detail.ManageDetailActivity.Companion.ID_FIRST_MANAGE
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.subscribeByAction
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_recent_manage.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject


class RecentManageFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }
    val isEmpty: ObservableBoolean = ObservableBoolean(false)

    @Inject
    lateinit var adapter: RecentManageAdapter
    lateinit var binding: FragmentRecentManageBinding



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recent_manage, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.isEmpty = isEmpty
        binding.recyclerListManageFragment.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        dis add adapter.clickManage
                .subscribeBy(
                        onNext = {
                            startActivity<ManageDetailActivity>(ManageDetailActivity.ID_MANAGE to it._id!!, ID_FIRST_MANAGE to it.idAplicacionUno!!)
                        }
                )

        dis add viewModel.getManages()
                .subscribeBy(
                        onNext = {
                            Log.d("MANEJOS", it.toString())
                            isEmpty.set(it.isEmpty())
                            adapter.recentManages = it
                        }
                )

        dis add btnAddManageFragment.clicks()
                .subscribeByAction(
                        onNext = {
                            startActivity<AddManageActivity>()
                        },
                        onHttpError = {},
                        onError = {}
                )
    }


    companion object {
        fun instance(): RecentManageFragment = RecentManageFragment()
    }

}
