package com.example.cristian.myapplication.ui.menu.bovine


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.couchbase.lite.internal.support.Log
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.di.FragmentScope
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListBovineAdapter
import com.example.cristian.myapplication.ui.bovine.AddBovineActivity
import com.example.cristian.myapplication.ui.bovine.DetailBovineActivity
import com.example.cristian.myapplication.ui.bovine.RemoveBovineActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByAction
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_list_bovine.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class ListBovineFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }

    @Inject
    lateinit var adapter: ListBovineAdapter
    val dis: LifeDisposable = LifeDisposable(this)

    private val idFinca: String by lazy { viewModel.getFarmId() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_bovine, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerListBovine.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getBovine(idFinca)
                .subscribeBy(
                        onSuccess = {
                            adapter.bovines = it
                            android.util.Log.d("BOVINOS", it.toString())
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add adapter.onClickBovine
                .subscribeBy(
                        onNext = {
                            startActivity<DetailBovineActivity>(BOVINE to it)
                        },
                        onComplete = {
                            Log.i("Bovine","On complete bovine")
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add adapter.onClickDelete
                .subscribeByAction(
                        onNext = {
                            startActivity<RemoveBovineActivity>(BOVINE to it)
                        },
                        onHttpError = {},
                        onError = {}
                )

        dis add btnAddBovine.clicks()
                .subscribe {
                    goToAddBovine()
                }
    }

    fun goToAddBovine() {
        startActivity<AddBovineActivity>()
    }

    companion object {
        val BOVINE : String = "bovino"
        val EXTRA_ID:String = "idBovino"
        fun instance():ListBovineFragment = ListBovineFragment()
    }



}
