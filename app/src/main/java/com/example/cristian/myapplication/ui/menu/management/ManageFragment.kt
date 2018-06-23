package com.example.cristian.myapplication.ui.menu.management

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentManageBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ManageBovineAdapter
import com.example.cristian.myapplication.ui.groups.SelectActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByAction
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_manage.*
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivityForResult

import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class ManageFragment : Fragment(),Injectable{

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    @Inject
    lateinit var adapter: ManageBovineAdapter
    lateinit var binding: FragmentManageBinding
    val isEmpty: ObservableBoolean = ObservableBoolean(false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage, container, false)
        binding.isEmpty = isEmpty
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        recyclerListManageFragment.adapter = adapter

        dis add viewModel.getManagement(idFinca)
                .subscribeBy (
                        onSuccess = {
                            isEmpty.set(it.isEmpty())
                            adapter.manage = it
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add btnAddManageFragment.clicks()
                .subscribeByAction(
                        onNext = {
                            startActivityForResult<SelectActivity>(221,"EXTRA_COLOR" to 5)
                        },
                        onHttpError = {},
                        onError = {}
                )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            toast("Funca !!${data!!.extras["bovines"]}")
        }
    }
    companion object {
        fun instance():ManageFragment = ManageFragment()
    }
}
