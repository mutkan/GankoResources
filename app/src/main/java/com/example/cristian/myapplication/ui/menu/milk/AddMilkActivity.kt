package com.example.cristian.myapplication.ui.menu.milk

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Produccion
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_milk.*
import kotlinx.android.synthetic.main.activity_add_milk_bovine.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class AddMilkActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener{

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MilkViewModel by lazy { buildViewModel<MilkViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val idBovino: String by lazy { intent.extras.getString(AddMilkActivity.EXTRA_ID) }
    lateinit var datePicker: DatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_milk)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Agregar Produccion de Leche")
        datePicker = DatePickerDialog(this, AddMilkBvnActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH)+1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        onDateSet(null, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH)+1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        fixColor(3)
    }

    override fun onResume() {
        super.onResume()

        dis add btnAddMilk.clicks()
                .flatMap {
                    validateForm(R.string.empty_fields, dateAddMilk.text.toString(), spinnerAddMilk.selectedItem.toString(),
                            priceAddMilk.text.toString(), litterAddMilk.text())
                }
                .flatMapSingle {
                    viewModel.addMilkProduction(
                           // Produccion(null, null, null, idBovino, it[1], it[0].toDate())
                    )
                }.subscribeBy(
                        onComplete = {
                            Log.i("COUCH", "complete")

                        },
                        onNext = {
                            Log.i("COUCH", "next")
                            finish()

                        },
                        onError = {
                            toast(it.message!!)

                        }

                )

        dis add btnCancelMilkBovine.clicks()
                .subscribe {
                    finish()
                }

        dis add dateAddMilkBovine.clicks()
                .subscribe { datePicker.show() }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateAddMilkBovine.text = "$dayOfMonth/${month+1}/$year"

    }
    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
