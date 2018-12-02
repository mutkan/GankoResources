package com.ceotic.ganko.ui.search

import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.models.RegistroManejo
import com.ceotic.ganko.data.models.RegistroVacuna
import com.ceotic.ganko.data.models.Sanidad
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.SearchAdapter
import com.ceotic.ganko.ui.bovine.DetailBovineActivity
import com.ceotic.ganko.ui.menu.health.detail.HealthDetailActivity
import com.ceotic.ganko.ui.menu.management.detail.ManageDetailActivity
import com.ceotic.ganko.ui.menu.management.detail.ManageDetailActivity.Companion.ID_FIRST_MANAGE
import com.ceotic.ganko.ui.menu.vaccines.detail.VaccineDetailActivity
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.fixColor
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class SearchActivity : AppCompatActivity(), Injectable {

    val selectedFragment: Int by lazy { intent.getIntExtra(SELECTED_FRAGMENT, 2) }

    @Inject
    lateinit var adapter: SearchAdapter

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: SearchViewModel by lazy { buildViewModel<SearchViewModel>(factory) }

    val dis = LifeDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when (selectedFragment) {
            MANAGE -> {
                fixColor(5)
                supportActionBar?.title = "BUSCAR MANEJOS"
                searchField.hint = "Busque por tipo de manejo"
            }
            STRAW -> {
                fixColor(9)
                supportActionBar?.title = "BUSCAR PAJILLAS"
                searchField.hint = "Busque por canastilla o identificador"
            }
            HEALTH -> {
                fixColor(8)
                supportActionBar?.title = "BUSCAR SANIDAD"
                searchField.hint = "Busque por diagnostico o evento"
            }
            VACCINE -> {
                fixColor(7)
                supportActionBar?.title = "BUSCAR VACUNAS"
                searchField.hint = "Busque por nombre de vacuna"
            }
            BOVINE -> {
                fixColor(2)
                supportActionBar?.title = "BUSCAR BOVINOS"
                searchField.hint = "Busque por identificador"
            }

            else -> {
                fixColor(4)
                supportActionBar?.title = "BUSCAR ALIMENTACION"
                searchField.hint = "Busque por tipo de alimento"
            }
        }
        val manager = LinearLayoutManager(this)
        searchList.layoutManager = manager
        searchList.adapter = adapter

    }

    override fun onResume() {
        super.onResume()

        dis add adapter.onClick.subscribe {
            when(it){
                is Bovino -> startActivity<DetailBovineActivity>(BOVINE_OBJECT to it)
                is RegistroManejo -> startActivity<ManageDetailActivity>(ManageDetailActivity.ID_MANAGE to it._id!!, ID_FIRST_MANAGE to it.idAplicacionUno!!)
                is RegistroVacuna -> startActivity<VaccineDetailActivity>(VaccineDetailActivity.ID_VACCINE to it._id!!, VaccineDetailActivity.ID_FIRST_VACCINE to it.idAplicacionUno!!)
                is Sanidad -> startActivity<HealthDetailActivity>(HealthDetailActivity.ID_HEALTH to it._id!!, HealthDetailActivity.ID_FIRST_HEALTH to it.idAplicacionUno!!)
            }
        }

        RxTextView.textChanges(searchField)
                .subscribe {
                    when (selectedFragment) {
                        MANAGE -> dis add viewModel.searchManage(it.toString()).subscribeBy(
                                onSuccess = {
                                    dis add viewModel.returnSearchType(it, selectedFragment).subscribeBy(
                                            onSuccess = {
                                                if (it.isEmpty()) emptySearchList.visibility = View.VISIBLE
                                                else emptySearchList.visibility = View.GONE
                                                adapter.data = it as MutableList<SearchType>
                                            },
                                            onError = { toast(it.message!!) }
                                    )
                                },
                                onError = { toast(it.message!!) }
                        )
                        VACCINE -> dis add viewModel.searchVaccine(it.toString()).subscribeBy(
                                onSuccess = {
                                    dis add viewModel.returnSearchType(it, selectedFragment).subscribeBy(
                                            onSuccess = {
                                                if (it.isEmpty()) emptySearchList.visibility = View.VISIBLE
                                                else emptySearchList.visibility = View.GONE
                                                adapter.data = it as MutableList<SearchType>
                                            },
                                            onError = { toast(it.message!!) }
                                    )
                                },
                                onError = { toast(it.message!!) }
                        )
                        HEALTH -> dis add viewModel.searchHealth(it.toString()).subscribeBy(
                                onSuccess = {
                                    dis add viewModel.returnSearchType(it, selectedFragment).subscribeBy(
                                            onSuccess = {
                                                if (it.isEmpty()) emptySearchList.visibility = View.VISIBLE
                                                else emptySearchList.visibility = View.GONE
                                                adapter.data = it as MutableList<SearchType>
                                            },
                                            onError = { toast(it.message!!) }
                                    )
                                },
                                onError = { toast(it.message!!) }
                        )
                        BOVINE -> dis add viewModel.searchBovine(it.toString()).subscribeBy(
                                onSuccess = {
                                    dis add viewModel.returnSearchType(it, selectedFragment).subscribeBy(
                                            onSuccess = {
                                                if (it.isEmpty()) emptySearchList.visibility = View.VISIBLE
                                                else emptySearchList.visibility = View.GONE
                                                adapter.data = it as MutableList<SearchType>
                                            },
                                            onError = { toast(it.message!!) }
                                    )
                                },
                                onError = { toast(it.message!!) }
                        )
                        STRAW -> dis add viewModel.searchStraw(it.toString()).subscribeBy(
                                onSuccess = {
                                    dis add viewModel.returnSearchType(it, selectedFragment).subscribeBy(
                                            onSuccess = {
                                                if (it.isEmpty()) emptySearchList.visibility = View.VISIBLE
                                                else emptySearchList.visibility = View.GONE
                                                adapter.data = it as MutableList<SearchType>
                                            },
                                            onError = { toast(it.message!!) }
                                    )
                                },
                                onError = { toast(it.message!!) }
                        )
                        else -> dis add viewModel.searchAlimentacion(it.toString()).subscribeBy(
                                onSuccess = {
                                    dis add viewModel.returnSearchType(it, selectedFragment).subscribeBy(
                                            onSuccess = {
                                                if (it.isEmpty()) emptySearchList.visibility = View.VISIBLE
                                                else emptySearchList.visibility = View.GONE
                                                adapter.data = it as MutableList<SearchType>
                                            },
                                            onError = { toast(it.message!!) }
                                    )
                                },
                                onError = { toast(it.message!!) }
                        )
                    }
                }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val SELECTED_FRAGMENT = "selectedFragment"
        const val MANAGE = 6
        const val VACCINE = 8
        const val HEALTH = 9
        const val BOVINE = 2
        const val STRAW = 10
        const val FEED = 5
        const val BOVINE_OBJECT= "bovino"

    }

    class SearchType(var item: Any, var type: Int)
}
