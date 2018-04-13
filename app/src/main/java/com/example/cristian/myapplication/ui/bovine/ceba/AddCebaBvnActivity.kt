package com.example.cristian.myapplication.ui.bovine.ceba

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Ceba
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.toDate
import com.example.cristian.myapplication.util.validateForm
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_ceba.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class AddCebaBvnActivity : AppCompatActivity(),Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: CebaViewModel by lazy { buildViewModel<CebaViewModel>(factory) }

    val dis:LifeDisposable = LifeDisposable(this)

    val idBovino = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ceba)
    }

    override fun onResume() {
        super.onResume()
        dis add btnAddCebaBvn.clicks()
                .flatMap { validateForm(R.string.empty_fields,dateAddCebaBvn.text.toString(),weightAddCebaBvn.text.toString()) }
                .flatMapSingle {
                    viewModel.addCeba(Ceba("",idBovino,it[0].toDate(),it[1].toFloat(),0f)) }
                .subscribeBy(
                        onNext = {
                            toast("Ceba Agregada Exitosamente")
                            finish()
                        },
                        onComplete = {
                            toast("on complete")
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )
    }
}
