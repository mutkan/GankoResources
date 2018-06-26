package com.example.cristian.myapplication.ui.menu.management


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentRecentManageBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.RecentManageAdapter
import com.example.cristian.myapplication.ui.manage.AddManageActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByAction
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_recent_manage.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


class RecentManageFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    @Inject
    lateinit var adapter: RecentManageAdapter
    lateinit var binding: FragmentRecentManageBinding

    //val isEmpty: ObservableBoolean = ObservableBoolean(false)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recent_manage, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        recyclerListManageFragment.adapter = adapter

        dis add viewModel.getManagement(idFinca)
                .subscribeBy (
                        onSuccess = {
                            //isEmpty.set(it.isEmpty())
                            adapter.recentManages = it
                        },
                        onError = {
                            toast(it.message!!)
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
