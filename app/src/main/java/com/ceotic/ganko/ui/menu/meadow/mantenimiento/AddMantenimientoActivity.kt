package com.ceotic.ganko.ui.menu.meadow.mantenimiento

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Mantenimiento
import com.ceotic.ganko.data.models.Pradera
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.menu.meadow.MeadowViewModel
import com.ceotic.ganko.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_mantenimiento.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class AddMantenimientoActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel: MeadowViewModel by lazy { buildViewModel<MeadowViewModel>(factory) }

    val meadow : Pradera by lazy { intent.getParcelableExtra<Pradera>(MEADOW) }
    lateinit var datePicker: DatePickerDialog
    val dis = LifeDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_mantenimiento)
        title = "Agregar Mantenimiento"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        datePicker = DatePickerDialog(this, AddMantenimientoActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        onDateSet(null, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        maintenanceQuantity.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when {
                    maintenancePrice.text() == "" -> maintenanceTotal.setText("0")
                    maintenanceQuantity.text() == "" -> maintenanceTotal.setText("0")
                    else -> maintenanceTotal.setText(""+(maintenancePrice.text().toFloat()*maintenanceQuantity.text().toFloat()))
                }
            }
        })

        maintenancePrice.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when {
                    maintenanceQuantity.text() == "" -> maintenanceTotal.setText("0")
                    maintenancePrice.text() == "" -> maintenanceTotal.setText("0")
                    else -> maintenanceTotal.setText(""+(maintenancePrice.text().toFloat()*maintenanceQuantity.text().toFloat()))
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()

        dis add btnCancel.clicks()
                .subscribe { finish() }

        dis add btnAdd.clicks()
                .flatMap { validateForm(R.string.empty_fields,maintenanceDate.text.toString(),maintenanceProduct.text(),
                        maintenanceQuantity.text(),maintenancePrice.text(),maintenanceTotal.text()) }
                .flatMapSingle {
                    val mantenimiento = Mantenimiento(it[0].toDate(),it[1],it[2].toFloat(),it[3].toFloat(),it[4].toFloat(), meadow.tipoGraminea)
                    meadow.mantenimiento!!.add(mantenimiento)
                    viewmodel.updateMeadow(meadow)
                }.subscribeBy (
                        onNext = {
                            toast("Mantenimiento Guardado")
                            finish()
                        },
                        onComplete = {
                            toast("Error inesperado")
                        },
                        onError = {
                            it.printStackTrace()
                            toast("Error inesperado")
                        }
                )

        dis add maintenanceDate.clicks()
                .subscribe { datePicker.show() }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        maintenanceDate.text = "$dayOfMonth/${month+1}/$year"

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    companion object {
        const val MEADOW = "meadow"
    }
}
