package com.example.cristian.myapplication.ui.menu.bovine


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListBovineAdapter
import com.example.cristian.myapplication.ui.bovine.AddBovineActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_list_bovine.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject
import org.jetbrains.anko.support.v4.toast

class ListBovineFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }

    @Inject
    lateinit var adapter: ListBovineAdapter
    val dis: LifeDisposable = LifeDisposable(this)

    lateinit var idFinca: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_bovine, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerListBovine.adapter = adapter
        idFinca = "1"
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getBovine(idFinca)
                .subscribeBy(
                        onSuccess = {
                            adapter.bovines = it
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )


        dis add btnAddBovine.clicks()
                .subscribe {
                    goToAddBovine()
                }
    }

    fun goToAddBovine() {
        startActivity<AddBovineActivity>()
    }


}
