package com.example.cristian.myapplication.ui.menu.management


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.RegistroManejo
import com.example.cristian.myapplication.databinding.FragmentPendingManageBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.PendingManageAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


class PendingManageFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentPendingManageBinding
    @Inject
    lateinit var adapter: PendingManageAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    val skiped: PublishSubject<RegistroManejo> = PublishSubject.create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending_manage, container, false)
        binding.isEmpty = isEmpty
        binding.recyclerPendingManage.adapter = adapter
        return binding.root
    }

    companion object {
        fun instance(): PendingManageFragment = PendingManageFragment()
    }


}
