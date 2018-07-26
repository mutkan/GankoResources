package com.example.cristian.myapplication.ui.menu.vaccines

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.data.models.ProxStates.Companion.APPLIED
import com.example.cristian.myapplication.data.models.ProxStates.Companion.NOT_APPLIED
import com.example.cristian.myapplication.data.models.toGrupo
import com.example.cristian.myapplication.databinding.ActivityAddVaccineBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.groups.GroupFragment
import com.example.cristian.myapplication.ui.groups.ReApplyActivity
import com.example.cristian.myapplication.ui.groups.ReApplyActivity.Companion.EXTRA_ID
import com.example.cristian.myapplication.ui.groups.ReApplyActivity.Companion.REQUEST_CODE
import com.example.cristian.myapplication.ui.groups.SelectActivity
import com.example.cristian.myapplication.ui.groups.SelectActivity.Companion.REQUEST_SELECT
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_vaccine.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.startActivityForResult
import java.util.*
import javax.inject.Inject

class AddVaccineActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    private val edit: Boolean by lazy { intent.getBooleanExtra("edit", false) }
    lateinit var previousVaccine: RegistroVacuna
    val dis: LifeDisposable = LifeDisposable(this)
    var groupFragment: GroupFragment? = null
    var group: Group? = null
    var bovines: List<String>? = null
    var noBovines: List<String>? = null
    val calendar: Calendar by lazy { Calendar.getInstance() }
    lateinit var binding: ActivityAddVaccineBinding
    val unidades: Array<String> by lazy { resources.getStringArray(R.array.time_units) }
    val datePicker: DatePickerDialog by lazy {
        DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }
    var dosisVacuna: Int = 0
        set(value) {
            field = value
            otherDose.visibility = if (value == -1) View.VISIBLE else View.GONE
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_vaccine)
        binding.edit = edit
        fixColor(7)
        title = "Registrar Vacuna"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        if (edit) {
            previousVaccine = intent!!.getParcelableExtra(PREVIOUS_VACCINE)
            startActivityForResult<ReApplyActivity>(REQUEST_CODE, EXTRA_ID to previousVaccine._id!!)
            setEdit()
        } else {
            startActivityForResult<SelectActivity>(REQUEST_SELECT,
                    SelectActivity.EXTRA_COLOR to 7)
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun setupGroupFragment() {
        if ((group != null || bovines != null) && groupFragment == null) {
            groupFragment = if (group != null) GroupFragment.instance(7, group!!)
            else GroupFragment.instance(12, bovines!!)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.vaccinesContainer, groupFragment)
                    .commit()

            dis add groupFragment!!.ids
                    .filter { group == null }
                    .subscribe { bovines = it }
        }
    }

    override fun onResume() {
        super.onResume()
        setupGroupFragment()

        dis add btnAcceptVaccine.clicks()
                .flatMap { validateFields() }
                .flatMapSingle {
                    val vaccine = createVaccine(it)
                    if (edit) viewModel.inserVaccine(vaccine).flatMap {
                        viewModel.updateVaccine(previousVaccine.apply { estadoProximo = APPLIED })
                    }
                    else viewModel.inserFirstVaccine(vaccine)
                }

                .subscribeBy(
                        onNext = {
                            finish()
                        },
                        onError = {
                            Log.e("ERROR", it.message, it)
                        }
                )

        dis add btnCancelVaccine.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )

        dis add vaccineDose.checkedChanges()
                .subscribeBy(
                        onNext = {
                            dosisVacuna = when (it) {
                                R.id.fiveMl -> 5
                                R.id.twoMl -> 2
                                else -> -1
                            }
                        }
                )

        dis add vaccinationDate.clicks()
                .subscribeBy(
                        onNext = {
                            datePicker.apply {
                                updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH))
                                datePicker.tag = "vaccinationDate"
                                show()
                            }
                        }
                )

        dis add revaccinationRequired.checkedChanges()
                .subscribeBy(
                        onNext = {
                            if (it) {
                                showNextApplicationGroup()

                            } else {
                                hideNextApplicationGroup()
                            }
                        }
                )

    }

    private fun hideNextApplicationGroup() {
        TransitionManager.beginDelayedTransition(contentView!! as ViewGroup)
        nextApplicationGroup.apply {
            visibility = View.GONE
            translationY = resources.getDimension(R.dimen.select_bar)
        }
    }

    private fun showNextApplicationGroup() {
        TransitionManager.beginDelayedTransition(contentView!! as ViewGroup)
        nextApplicationGroup.apply {
            visibility = View.VISIBLE
            translationY = 0f
        }
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val sDate = "$dayOfMonth/${month + 1}/$year"
        view?.let {
            when (it.tag) {
                "vaccinationDate" -> {
                    vaccinationDate.setText(sDate)
                }
            }
        }
    }

    private fun validateFields(): Observable<List<String>> {
        val fecha = vaccinationDate.text()
        val valor = vaccineValue.text()
        val nombreVacuna = otherVaccine.text()
        val proximaAplicacion = nextApplicationVaccine.text()
        return if (revaccinationRequired.isChecked) validateForm(R.string.empty_fields, fecha, valor, nombreVacuna, proximaAplicacion) else validateForm(R.string.empty_fields, fecha, valor, nombreVacuna)
    }

    private fun createVaccine(fields: List<String>): RegistroVacuna {
        val fecha = fields[0].toDate()
        val valor = fields[1].toInt()
        val idFinca = viewModel.getFarmId()
        val nombreVacuna = fields[2]
        val dosis = when {
            dosisVacuna != -1 -> dosisVacuna
            otherDose.text() != "" -> otherDose.text().toInt()
            else -> null
        }
        val proximaAplicacion = if (revaccinationRequired.isChecked) fields[3].toInt() else null
        val unidadTiempo = timeUnitsSpinner.selectedItem.toString()
        val fechaProx = when (unidadTiempo) {
            "Horas" -> fecha.add(Calendar.HOUR, proximaAplicacion)
            "Días" -> fecha.add(Calendar.DATE, proximaAplicacion)
            "Meses" -> fecha.add(Calendar.MONTH, proximaAplicacion)
            else -> fecha.add(Calendar.YEAR, proximaAplicacion)
        }
        return if (!edit) {
            RegistroVacuna(nombre = nombreVacuna, titulo = nombreVacuna, descripcion = "Aplicación de vacuna contra $nombreVacuna, $dosis ml", dosisMl = dosis, frecuencia = proximaAplicacion, fecha = fecha, fechaProxima = fechaProx, valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, unidadFrecuencia = unidadTiempo, estadoProximo = NOT_APPLIED)
        } else {
            RegistroVacuna(idDosisUno = previousVaccine.idDosisUno, titulo = nombreVacuna, descripcion = "Aplicación de vacuna contra $nombreVacuna, $dosis ml", nombre = nombreVacuna, dosisMl = dosis, frecuencia = proximaAplicacion, fecha = fecha, fechaProxima = fechaProx, valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, unidadFrecuencia = unidadTiempo, estadoProximo = NOT_APPLIED, noBovinos = noBovines)
        }
    }

    private fun setEdit() {
        otherVaccine.setText(previousVaccine.nombre)
        nextApplicationVaccine.setText(previousVaccine.frecuencia.toString())
        revaccinationRequired.isChecked = previousVaccine.fechaProxima != null
        if (previousVaccine.dosisMl != null && previousVaccine.dosisMl != 5 && previousVaccine.dosisMl != 2) otherDose.setText(previousVaccine.dosisMl.toString())
        val idRadioDose = when (previousVaccine.dosisMl) {
            5 -> R.id.fiveMl
            2 -> R.id.twoMl
            else -> R.id.otherDoseRadio
        }
        vaccineDose.apply {
            check(idRadioDose)
            isEnabled = false
        }
        val unidadTiempo = unidades.indexOf(previousVaccine.unidadFrecuencia)
        timeUnitsSpinner.apply {
            setSelection(unidadTiempo)
            isEnabled = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SelectActivity.REQUEST_SELECT -> {
                if (resultCode == Activity.RESULT_OK) {
                    group = data?.extras?.getParcelable(SelectActivity.DATA_GROUP)
                    bovines = data?.extras?.getStringArray(SelectActivity.DATA_BOVINES)?.toList()
                    group?.let {
                        bovines = it.bovines
                    }
                } else {
                    finish()
                }
            }
            REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    bovines = data?.extras?.getStringArray(ReApplyActivity.DATA_BOVINES)?.toList()
                    noBovines = data?.extras?.getStringArray(ReApplyActivity.DATA_NO_BOVINES)?.toList()
                } else {
                    finish()
                }
            }
        }


    }

    companion object {
        const val PREVIOUS_VACCINE = "previousVaccine"
    }
}
