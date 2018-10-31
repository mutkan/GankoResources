package com.ceotic.ganko.ui.menu.bovine


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.couchbase.lite.internal.support.Log
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.models.Filter
import com.ceotic.ganko.databinding.FragmentListBovineBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListBovineAdapter
import com.ceotic.ganko.ui.bovine.AddBovineActivity
import com.ceotic.ganko.ui.bovine.DetailBovineActivity
import com.ceotic.ganko.ui.bovine.RemoveBovineActivity
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_list_bovine.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class ListBovineFragment : Fragment(), Injectable {

    @Inject
    lateinit var adapter: ListBovineAdapter
    val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }

    lateinit var binding: FragmentListBovineBinding
    private var filter: Filter = Filter()
    private val idFinca: String by lazy { viewModel.getFarmId() }
    val isEmpty: ObservableBoolean = ObservableBoolean(false)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_bovine, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerListBovine.adapter = adapter
        binding.isEmpty = isEmpty
    }


    override fun onResume() {
        super.onResume()

        dis add viewModel.getBovineByFilter(idFinca)
                .subscribeBy(
                        onNext = {
                            isEmpty.set(it.isEmpty())
                            adapter.bovines = it

                        },
                        onError = {
                            Log.e("ERROR", it.message, it)
                            toast(it.message!!)
                        }
                )

        dis add adapter.onClickBovine
                .subscribeBy(
                        onNext = {
                            startActivity<DetailBovineActivity>(BOVINE to it)
                        },
                        onComplete = {
                            Log.i("Bovine", "On complete bovine")
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add adapter.onClickDelete
                .subscribeBy(
                        onNext = {
                            startActivity<RemoveBovineActivity>(BOVINE to it)
                        },
                        onError = {}
                )

        dis add btnAddBovine.clicks()
                .subscribe {
                    goToAddBovine()
                }

    }

    fun filterBovines() {
        var listFilter: List<Bovino> = emptyList()
        dis add viewModel.getBovinesFilter(idFinca)
                .subscribeBy(
                        onSuccess = {
                            isEmpty.set(it.isEmpty())
                            filter.meat_purpose = true
                            filter.milk_purpose = true

                            if (filter.milk_purpose) listFilter = listFilter.plus(it.filter { it.proposito == "Lecheria" })
                            if (filter.meat_purpose) listFilter = listFilter.plus(it.filter { it.proposito == "Ceba" })
                            if (filter.both_purpose) listFilter = listFilter.plus(it.filter { it.proposito == "Ambos" })
                            if (filter.retired) listFilter = listFilter.plus(it.filter { it.retirado == true })

                            adapter.bovines = listFilter
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )
    }

    fun goToAddBovine() {
        startActivity<AddBovineActivity>()
    }

    companion object {
        val BOVINE: String = "bovino"
        val EXTRA_ID: String = "idBovino"
        fun instance(): ListBovineFragment = ListBovineFragment()
    }


}
