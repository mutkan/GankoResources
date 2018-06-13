package com.example.cristian.myapplication.ui.menu.management

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage, container, false)
    }

    override fun onResume() {
        super.onResume()
        recyclerListManageFragment.adapter = adapter

        dis add viewModel.getManagement(idFinca)
                .subscribeBy (
                        onSuccess = {
                            adapter.manage = it
                            if (it.isEmpty()) toast(R.string.empty_list)
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add btnAddManageFragment.clicks()
                .subscribeByAction(
                        onNext = {
                            startActivityForResult(intentFor<SelectActivity>(),221)
                        },
                        onHttpError = {},
                        onError = {}
                )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            toast("Funca !!")
        }
    }
    companion object {
        fun instance():ManageFragment = ManageFragment()
    }
}
