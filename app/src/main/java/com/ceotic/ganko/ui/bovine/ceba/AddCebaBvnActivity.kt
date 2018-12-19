package com.ceotic.ganko.ui.bovine.ceba

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Ceba
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_ceba.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class AddCebaBvnActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: CebaViewModel by lazy { buildViewModel<CebaViewModel>(factory) }

    val dis: LifeDisposable = LifeDisposable(this)

    lateinit var datePicker: DatePickerDialog

    lateinit var idBovino: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ceba)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Agregar Ceba")

        intent.extras?.run {
            idBovino = getString(EXTRA_ID)
        }

        datePicker = DatePickerDialog(this, AddMilkBvnActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        onDateSet(null, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

    }

    override fun onResume() {
        super.onResume()
        dis add btnAddCebaBvn.clicks()
                .flatMapMaybe { viewModel.lastCeba(idBovino) }
                .map { it.peso to it.fecha }
                .defaultIfEmpty(0f to Date())
                .flatMap { prev ->
                    validateForm(R.string.empty_fields, dateAddCebaBvn.text.toString(), weightAddCebaBvn.text.toString())
                            .map { prev to it }
                }
                .flatMapSingle { (prev, form)->
                    val current = form[0].toDate()
                    val weight = form[1].toFloat()
                    val gain: Float = if (prev.first != 0f) {
                        val milis = current.time - prev.second!!.time
                        val days = Math.ceil(milis.toDouble() / 84_400_000)
                        ((weight - prev.first!!) * 1000 / days).toFloat()
                    } else 0f

                    viewModel.addCeba(Ceba(null, null, null, "", idBovino, current, weight, gain, false))
                }
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
        dis add dateAddCebaBvn.clicks()
                .subscribe { datePicker.show() }


        dis add btnCancelCebaBvn.clicks()
                .subscribeByAction(
                        onNext = {
                            finish()
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateAddCebaBvn.text = "$dayOfMonth/${month + 1}/$year"
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
