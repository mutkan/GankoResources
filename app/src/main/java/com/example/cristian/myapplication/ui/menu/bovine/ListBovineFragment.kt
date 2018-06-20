package com.example.cristian.myapplication.ui.menu.bovine


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.couchbase.lite.internal.support.Log
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Filter
import com.example.cristian.myapplication.databinding.FragmentListBovineBinding
import com.example.cristian.myapplication.databinding.TemplateZealBinding
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
import com.jakewharton.rxbinding2.view.selected
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_filter.*
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
    lateinit var binding: FragmentListBovineBinding
    private var filter:Filter = Filter()
    private val idFinca: String by lazy { viewModel.getFarmId() }
    val isEmpty: ObservableBoolean = ObservableBoolean(false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_bovine, container, false )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerListBovine.adapter = adapter
        binding.isEmpty = isEmpty
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getBovine(idFinca)
                .subscribeBy(
                        onSuccess = {
                            isEmpty.set(it.isEmpty())
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

        check_milk.setOnCheckedChangeListener { _, b -> filter.milk_purpose = b
        filterBovines()     }
        check_meat.setOnCheckedChangeListener { _, b -> filter.meat_purpose=b
        filterBovines()     }
        check_both.setOnCheckedChangeListener { _, b -> filter.both_purpose=b
        filterBovines()     }
        check_retired.setOnCheckedChangeListener { _, b -> filter.retired=b
        filterBovines()     }
        check_weaning.setOnCheckedChangeListener{_,b -> filter.destete=b
        filterBovines()     }
        check_service.setOnCheckedChangeListener { _, b ->  filter.servicio=b
        filterBovines()     }

    }

    fun filterBovines(){
        dis add viewModel.getBovinesFilter(idFinca,filter)
                .subscribeBy(
                        onSuccess = {
                            isEmpty.set(it.isEmpty())

                           when{filter.milk_purpose -> {it.filter { it.proposito == "leche" } }
                                filter.meat_purpose -> {it.filter { it.proposito == "carne" } }
                                filter.both_purpose -> {it.filter { it.proposito == "ambos" } }
                                filter.retired      -> {it.filter { it.retirado == true     } }
                                filter.destete ->      {it.filter { it.destete == true     } }
                                filter.servicio ->     {it.filter { it.servicios!![0].confirmacion ==true  } }

                           }
                            adapter.bovines = it
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
        val BOVINE : String = "bovino"
        val EXTRA_ID:String = "idBovino"
        fun instance():ListBovineFragment = ListBovineFragment()
    }



}
