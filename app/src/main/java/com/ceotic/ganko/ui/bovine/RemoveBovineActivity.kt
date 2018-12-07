package com.ceotic.ganko.ui.bovine

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker

import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.databinding.ActivityRemoveBovineBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.util.*
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_remove_bovine.*
import org.jetbrains.anko.toast

import java.util.*
import javax.inject.Inject

class RemoveBovineActivity : AppCompatActivity() , Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: BovineViewModel by lazy { buildViewModel<BovineViewModel>(factory) }
    lateinit var datePicker: DatePickerDialog
    lateinit var binding: ActivityRemoveBovineBinding
    val bovine: Bovino by lazy { intent.extras.getParcelable<Bovino>(BOVINE) }

    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remove_bovine)
        binding.bovino = bovine
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.remove_bovine)
        datePicker = DatePickerDialog(this, RemoveBovineActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    }

    override fun onResume() {
        super.onResume()

        dis add retirementDateRemoveBovine.clicks()
                .subscribe { datePicker.show() }

        dis add btnNextRemoveBovine.clicks()
                .flatMap { validateForm(R.string.empty_fields, retirementDateRemoveBovine.text.toString()) }
                .flatMapSingle {
                    val bovino = bovine
                    bovino.retirado = true
                    bovino.motivoSalida = when(reasonSpinnerRemoveBovine.selectedItemPosition){
                        0 -> "Venta"
                        1 -> "Perdida"
                        else -> "Muerte"
                    }
                    bovino.fechaSalida = retirementDateRemoveBovine.text.toString().toDate()
                    viewModel.updateBovine(bovino._id!!,bovino)
                }
                .subscribeByAction(
                        onNext = {
                            toast(getString(R.string.the_bovine_has_been_retired))
                            finish()
                        },
                        onHttpError = {},
                        onError = {}
                )

        dis add btnCancelRemoveBovine.clicks()
                .subscribe { finish() }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        retirementDateRemoveBovine.text = "$dayOfMonth/$month/$year"
    }


    companion object {
        val BOVINE: String = "bovino"
    }

}
