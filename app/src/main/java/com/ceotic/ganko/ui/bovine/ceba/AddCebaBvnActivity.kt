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

    val idBovino: String by lazy { intent.extras.getString(EXTRA_ID) }

    lateinit var datePicker: DatePickerDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ceba)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Agregar Ceba")
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
                .flatMap { validateForm(R.string.empty_fields, dateAddCebaBvn.text.toString(), weightAddCebaBvn.text.toString()) }
                .flatMapSingle {
                    viewModel.addCeba(Ceba(null, null, null, "",idBovino, it[0].toDate(), it[1].toFloat(), 0f,false))
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
        dateAddCebaBvn.text = "$dayOfMonth/${month+1}/$year"
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}