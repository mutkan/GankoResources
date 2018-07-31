package com.example.cristian.myapplication.ui.search

import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.SearchAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_search.*
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
                supportActionBar?.title = "BUSCAR MANEJOS"
                searchField.hint = "Busque por tipo de manejo"
            }
            STRAW -> {
                supportActionBar?.title = "BUSCAR PAJILLAS"
                searchField.hint = "Busque por canastilla o identificador"
            }
            HEALTH -> {
                supportActionBar?.title = "BUSCAR SANIDAD"
                searchField.hint = "Busque por diagnostico o evento"
            }
            VACCINE -> {
                supportActionBar?.title = "BUSCAR VACUNAS"
                searchField.hint = "Busque por nombre de vacuna"
            }
            BOVINE -> {
                supportActionBar?.title = "BUSCAR BOVINOS"
                searchField.hint = "Busque por identificador"
            }

            else -> {
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
    }

    class SearchType(var item: Any, var type: Int)
}
