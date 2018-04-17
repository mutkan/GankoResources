package com.example.cristian.myapplication.ui.bovine.ceba

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListCebaBovineAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_ceba.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class CebaBvnActivity : AppCompatActivity(),Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: CebaViewModel by lazy { buildViewModel<CebaViewModel>(factory) }

    @Inject
    lateinit var cebaAdapter:ListCebaBovineAdapter

    private val dis:LifeDisposable = LifeDisposable(this)

    var idBovino = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_ceba)
        recyclerListCeba.adapter = cebaAdapter
        recyclerListCeba.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getListCeba(idBovino)
                .subscribeBy(
                        onSuccess = {
                            cebaAdapter.data = it
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add btnGoToAddCeba.clicks()
                .subscribe {
                    startActivity<AddCebaBvnActivity>("idBovino" to idBovino)
                }
    }
}
