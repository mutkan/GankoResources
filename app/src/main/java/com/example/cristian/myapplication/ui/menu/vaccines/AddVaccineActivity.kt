package com.example.cristian.myapplication.ui.menu.vaccines

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.data.models.toGrupo
import com.example.cristian.myapplication.databinding.ActivityAddVaccineBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.groups.GroupFragment
import com.example.cristian.myapplication.ui.groups.SelectActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.itemSelections
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_vaccine.*
import org.jetbrains.anko.startActivityForResult
import java.util.*
import javax.inject.Inject

class AddVaccineActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    var groupFragment: GroupFragment? = null
    var group: Group? = null
    var bovines: List<String>? = null
    val calendar: Calendar by lazy { Calendar.getInstance() }
    lateinit var binding: ActivityAddVaccineBinding
    val datePicker: DatePickerDialog by lazy {
        DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }
    val vacunas: Array<String> by lazy { resources.getStringArray(R.array.vaccines) }
    val unidades: Array<String> by lazy { resources.getStringArray(R.array.time_units) }
    var nombreVacuna: String = ""
        set(value) {
            field = value
            otherVaccineGroup.visibility = if (value == "Otra") View.VISIBLE else View.GONE
            if (value != "Otra") {
                if (otherDose.visibility != View.GONE) otherDose.visibility = View.GONE
                if (nextApplicationTxt.visibility != View.GONE) nextApplicationTxt.visibility = View.GONE
                if (nextApplicationVaccine.visibility != View.GONE) nextApplicationVaccine.visibility = View.GONE
                revaccinationRequired.isChecked = false
            }

        }
    var dosisVacuna: Int = 0
        set(value) {
            field = value
            otherDose.visibility = if (value == -1) View.VISIBLE else View.GONE
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_vaccine)
//        binding.edit = true
//        binding.vaccineSpinner.isEnabled = false
//        binding.vaccineDose.isEnabled = false
        fixColor(7)
        title = "Registrar Vacuna"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        startActivityForResult<SelectActivity>(SelectActivity.REQUEST_SELECT,
                SelectActivity.EXTRA_COLOR to 7)

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun setupGroupFragment() {
        if ((group != null || bovines != null) && groupFragment == null) {
            groupFragment = if (group != null) GroupFragment.instance(12, group!!)
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
                    viewModel.inserVaccine(vaccine)
                }
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )
        dis add btnCancelVaccine.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )

        dis add vaccineSpinner.itemSelections()
                .subscribeBy(
                        onNext = {
                            nombreVacuna = vacunas[it]
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
                            if (it && nombreVacuna == "Otra") {
                                nextApplicationTxt.visibility = View.VISIBLE
                                nextApplicationVaccine.visibility = View.VISIBLE
                                timeUnitsSpinner.visibility = View.VISIBLE
                            } else {
                                nextApplicationTxt.visibility = View.GONE
                                nextApplicationVaccine.visibility = View.GONE
                                timeUnitsSpinner.visibility = View.GONE
                            }
                        }
                )

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
        val otraVacuna = otherVaccine.text()
        val otraDosis = otherDose.text()
        val proximaAplicacion = nextApplicationVaccine.text()
        val otra = if (nombreVacuna == "Otra") "1" else "0"
        val otraD = if (dosisVacuna == -1) "1" else "0"
        val otraA = if (revaccinationRequired.isChecked) "1" else "0"
        return when ("$otra$otraD$otraA") {
            "000" -> validateForm(R.string.empty_fields, fecha, valor)
            "100" -> validateForm(R.string.empty_fields, fecha, valor, otraVacuna)
            "110" -> validateForm(R.string.empty_fields, fecha, valor, otraVacuna, otraDosis)
            "101" -> validateForm(R.string.empty_fields, fecha, valor, otraVacuna, proximaAplicacion)
            else -> validateForm(R.string.empty_fields, fecha, valor, otraVacuna, otraDosis, proximaAplicacion)
        }
    }

    private fun createVaccine(fields: List<String>): RegistroVacuna {
        val fecha = fields[0].toDate()
        val valor = fields[1].toInt()
        val idFinca = viewModel.getFarmId()
        val nombreOtra = fields.getOrNull(2)
        val dosis = if (dosisVacuna != -1) dosisVacuna else {
            fields.getOrNull(3)?.toInt() ?: 0
        }
        val proximaAplicacion = if (revaccinationRequired.isChecked) fields.last().toInt() else null
        val unidadTiempo = unidades[timeUnitsSpinner.selectedItemPosition]
        val fechaProx = when (unidadTiempo) {
            "Horas" -> fecha.add(Calendar.HOUR, proximaAplicacion)
            "Días" -> fecha.add(Calendar.DATE, proximaAplicacion)
            "Meses" -> fecha.add(Calendar.MONTH, proximaAplicacion)
            else -> fecha.add(Calendar.YEAR, proximaAplicacion)
        }
        Log.d("BOVINOS", bovines.toString())
        return when (nombreVacuna) {
            "Fiebre Aftosa" -> RegistroVacuna(nombre = nombreVacuna, dosisMl = 5, frecuenciaMeses = 6, fecha = fecha, fechaProx = fecha.add(Calendar.MONTH, 6), valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, proxAplicado = false)
            "Rabia" -> RegistroVacuna(nombre = nombreVacuna, dosisMl = 5, frecuenciaMeses = 12, fecha = fecha, fechaProx = fecha.add(Calendar.MONTH, 12), valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, proxAplicado = false)
            "Carbón Sintomático" -> RegistroVacuna(nombre = nombreVacuna, dosisMl = 5, frecuenciaMeses = 12, fecha = fecha, fechaProx = fecha.add(Calendar.MONTH, 12), valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, proxAplicado = false)
            "Edema Maligno" -> RegistroVacuna(nombre = nombreVacuna, dosisMl = 5, frecuenciaMeses = 12, fecha = fecha, fechaProx = fecha.add(Calendar.MONTH, 12), valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, proxAplicado = false)
            "Septicemia Hemorrágica" -> RegistroVacuna(nombre = nombreVacuna, dosisMl = 5, frecuenciaMeses = 12, fecha = fecha, fechaProx = fecha.add(Calendar.MONTH, 12), valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, proxAplicado = false)
            "Carbón Bacteridiano" -> RegistroVacuna(nombre = nombreVacuna, dosisMl = 2, frecuenciaMeses = 12, fecha = fecha, fechaProx = fecha.add(Calendar.MONTH, 12), valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, proxAplicado = false)
            "Brucelosis" -> RegistroVacuna(nombre = nombreVacuna, dosisMl = 5, frecuenciaMeses = null, fecha = fecha, fechaProx = null, valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, proxAplicado = false)
            else -> RegistroVacuna(nombre = nombreOtra, dosisMl = dosis, frecuenciaMeses = proximaAplicacion, fecha = fecha, fechaProx = fechaProx, valor = valor, idFinca = idFinca, grupo = group?.toGrupo(), bovinos = bovines, proxAplicado = false)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SelectActivity.REQUEST_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                group = data?.extras?.getParcelable(SelectActivity.DATA_GROUP)
                bovines = data?.extras?.getStringArray(SelectActivity.DATA_BOVINES)?.toList()
            } else {
                finish()
            }
        }
    }
}
