package com.example.cristian.myapplication.ui.menu.meadow

import android.arch.lifecycle.ViewModelProvider
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.MeadowAlertAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_manage_meadow_alert.*
import org.jetbrains.anko.*
import javax.inject.Inject

class ManageMeadowAlertActivity : AppCompatActivity(),Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MeadowViewModel by lazy { buildViewModel<MeadowViewModel>(factory) }

    val dis: LifeDisposable = LifeDisposable(this)
    val adapter: MeadowAlertAdapter by lazy { MeadowAlertAdapter() }
    val idMeadow: String by lazy { intent.getStringExtra(MEADOWID) }
    var meadow = Pradera()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_meadow_alert)
        supportActionBar?.title = "Alertas Pradera ..."

        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.prairie_primary)))
        window.statusBarColor = ContextCompat.getColor(this, R.color.prairie_dark)

        listMeadowAlerts.layoutManager = LinearLayoutManager(this)
        listMeadowAlerts.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        dis add viewModel.getMeadow(idMeadow)
                .subscribeBy {
                    meadow = it
                    supportActionBar?.title = "Alertas Pradera " + it.identificador
                }
        dis add viewModel.getMeadowAlert(idMeadow)
                .subscribeBy {
                    adapter.data = it.toMutableList().asReversed()
                    if (it.isEmpty()) emptyListMeadowAlarm.visibility = View.VISIBLE
                    else emptyListMeadowAlarm.visibility = View.GONE
                }

        dis add btnAddMeadowAlarm.clicks()
                .subscribe { startActivity<AddMeadowAlertActivity>(MEADOWID to idMeadow) }

        dis add adapter.onClickDelete
                .subscribe { meadowAlarm ->
                    alert {
                        title = "¿Desea borrar esta alerta?"
                        yesButton {
                            viewModel.deleteMeadowAlert(meadowAlarm._id!!)
                                    .subscribeBy {
                                        toast("Alerta Eliminada Correctamente")
                                    }
                        }
                        noButton { }
                    }.show()
                }
    }

    companion object {
        const val MEADOWID = "meadow"
    }
}
