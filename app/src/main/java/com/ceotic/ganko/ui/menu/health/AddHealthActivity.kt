package com.ceotic.ganko.ui.menu.health

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Group
import com.ceotic.ganko.data.models.ProxStates
import com.ceotic.ganko.data.models.ProxStates.Companion.APPLIED
import com.ceotic.ganko.data.models.Sanidad
import com.ceotic.ganko.data.models.toGrupo
import com.ceotic.ganko.databinding.ActivityAddHealthBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.groups.GroupFragment
import com.ceotic.ganko.ui.groups.ReApplyActivity
import com.ceotic.ganko.ui.groups.ReApplyActivity.Companion.EXTRA_ID
import com.ceotic.ganko.ui.groups.ReApplyActivity.Companion.REQUEST_CODE
import com.ceotic.ganko.ui.groups.SelectActivity
import com.ceotic.ganko.ui.groups.SelectActivity.Companion.REQUEST_SELECT
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.*
import com.ceotic.ganko.work.NotificationWork
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_health.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddHealthActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: HealthViewModel by lazy { buildViewModel<HealthViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val menuViewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    private val edit: Boolean by lazy { intent.getBooleanExtra("edit", false) }
    lateinit var previousHealth: Sanidad
    private val farmId by lazy { menuViewModel.getFarmId() }
    lateinit var datePicker: DatePickerDialog
    lateinit var binding: ActivityAddHealthBinding
    val currentDate: Date = Date()
    var group: Group? = null
    var noBovines: List<String>? = null
    var bovines: List<String>? = null
    var groupFragment: GroupFragment? = null
    val unidades: Array<String> by lazy { resources.getStringArray(R.array.time_units) }
    val eventos: Array<String> by lazy { resources.getStringArray(R.array.health_event) }
    var unidadTiempo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_health)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Agregar sanidad")
        fixColor(8)
        title = getString(R.string.add_bovine)
        binding.page = 1
        datePicker = DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        onDateSet(null, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        if (edit) {
            previousHealth = intent!!.getParcelableExtra(PREVIOUS_HEALTH)
            startActivityForResult<ReApplyActivity>(REQUEST_CODE, EXTRA_ID to previousHealth._id!!)
            setEdit()
        } else {
            startActivityForResult<SelectActivity>(REQUEST_SELECT,
                    SelectActivity.EXTRA_COLOR to 8)
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
                    Log.d("No bovinos", noBovines.toString())
                } else {
                    finish()
                }
            }
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateAddHealth.setText("$dayOfMonth/${month + 1}/$year")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun setupGroupFragment() {
        if ((group != null || bovines != null) && groupFragment == null) {
            groupFragment = if (group != null) GroupFragment.instance(8, group!!)
            else GroupFragment.instance(8, bovines!!)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.healthContainer, groupFragment)
                    .commit()

            dis add groupFragment!!.ids
                    .filter { group == null }
                    .subscribe { bovines = it }
        }
    }

    override fun onResume() {
        super.onResume()
        setupGroupFragment()

        dis add btnBackHealth.clicks()
                .subscribeBy(
                        onNext = {
                            minusPage()
                        }
                )

        dis add btnCancelHealth.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )

        dis add dateAddHealth.clicks()
                .subscribe { datePicker.show() }

        dis add spinnerEvent.itemSelections()
                .subscribe { binding.otherSelect = it == 3 }

        dis add btnAddHealth.clicks()
                .flatMap {

                    validateForm(R.string.empty_fields, spinnerEvent.selectedItem.toString(),
                            if (binding.otherSelect) other.text.toString() else "other", diagnosis.text.toString(),
                            dateAddHealth.text.toString(), treatment_health.text.toString(), product_health.text.toString()
                    )

                }.subscribeBy(
                        onNext = {
                            plusPage()
                        }
                )

//        unidadTiempo = frecuencyOptionsHealth.selectedItem.toString()
        dis add frecuencyOptionsHealth.itemSelections()
                .subscribeBy(
                        onNext = {
                            unidadTiempo = unidades[it]
                        }
                )

        if (edit) {
            dis add btnFinalizeHealth.clicks()
                    .flatMap {

                        validateForm(R.string.empty_fields, dosis.text.toString(), frequency.text(),
                                product_value.text.toString(), attention_value.text.toString(), applicacion_number.text.toString())
                    }

                    .flatMapSingle {
                        val notifyTime = calculateNotifyTime()
                        val healthAplication = previousHealth.aplicacion!!.plus(1)
                        viewModel.addHealth(

                                Sanidad(null, null, null, farmId, spinnerEvent.selectedItem.toString(), frecuencyOptionsHealth.selectedItem.toString(),
                                        dateAddHealth.text.toString().toDate(), if(healthAplication < (previousHealth.numeroAplicaciones ?: 0)) fechaProxima1(dateAddHealth.text().toDate(), applicacion_number.text().toInt(), frequency.text().toInt()) else null,
                                        frequency.text().toInt(), spinnerEvent.selectedItem.toString(),
                                        if (binding.otherSelect) other.text() else null, diagnosis.text(), treatment_health.text(),
                                        product_health.text(), dosis.text(), null, applicacion_number.text().toInt(),
                                        healthAplication,
                                        if(observations_health.text()!= "") observations_health.text() else getString(R.string.no_observations), product_value.text().toInt(), attention_value.text().toInt(),
                                        group?.toGrupo(), bovines!!, unidadTiempo, noBovines!!, ProxStates.NOT_APPLIED, previousHealth.idAplicacionUno))



                    }.flatMapSingle {
                        viewModel.updateHealth(previousHealth.apply { estadoProximo = APPLIED })
                    }
                    .subscribeBy(
                            onNext = {
                                toast("Sanidad agregada exitosamente")
                                finish()
                            },

                            onComplete = {
                                toast("onComplete")
                            },
                            onError = {
                                Log.e("Error", it.message, it)
                            }
                    )

        } else {
            dis add btnFinalizeHealth.clicks()
                    .flatMap {
                        validateForm(R.string.empty_fields, dosis.text.toString(), frequency.text(),
                                product_value.text.toString(), attention_value.text.toString(), applicacion_number.text.toString())
                    }

                    .flatMapSingle {
                        val notifyTime = calculateNotifyTime()
                        viewModel.addFirstHealth(
                                Sanidad(null, null, null, farmId, spinnerEvent.selectedItem.toString(), frecuencyOptionsHealth.selectedItem.toString(),
                                        dateAddHealth.text.toString().toDate(), fechaProxima1(dateAddHealth.text().toDate(), applicacion_number.text().toInt(), frequency.text().toInt()),
                                        frequency.text().toInt(), spinnerEvent.selectedItem.toString(),
                                        if (binding.otherSelect) other.text() else null, diagnosis.text(), treatment_health.text(),
                                        product_health.text(), dosis.text(), null, applicacion_number.text().toInt(), 1,
                                        if(observations_health.text()!= "") observations_health.text() else getString(R.string.no_observations), product_value.text().toInt(), attention_value.text().toInt(),
                                        group?.toGrupo(), bovines!!, unidadTiempo, emptyList()), notifyTime
                        )

                    }
                    .subscribeBy(
                            onNext = {
                                toast("Sanidad agregada exitosamente")
                                finish()
                            },

                            onComplete = {
                                toast("onComplete")
                            },
                            onError = {
                                Log.e("Error", it.message, it)
                            }
                    )
        }
    }

    fun calculateNotifyTime():Long{
        val frequencyTime = frequency.text().toLong()
        val proxTime = when (unidadTiempo) {
            "Horas" -> frequencyTime
            "Días" -> frequencyTime * 24
            "Meses" -> frequencyTime * 24 * 30
            else -> frequencyTime * 24 * 30 * 12
        }
        return when (proxTime) {
            in 0..2 -> proxTime
            in 3..10 -> proxTime - 1
            in 11..36 -> proxTime - 3
            else -> proxTime - 24
        }
    }


    private fun setEdit() {
        val evento = eventos.indexOf(previousHealth.evento)
        spinnerEvent.apply {
            setSelection(evento)
            isEnabled = false
        }
        other.setText(previousHealth.otra.toString())
        diagnosis.setText(previousHealth.diagnostico.toString())
        treatment_health.setText(previousHealth.tratamiento.toString())
        product_health.setText(previousHealth.producto.toString())
        dosis.setText(previousHealth.dosis.toString())
        frequency.setText(previousHealth.frecuencia.toString())
        val unidadTiempo = unidades.indexOf(previousHealth.unidadFrecuencia)
        frecuencyOptionsHealth.apply {
            setSelection(unidadTiempo)
            isEnabled = false
        }
        applicacion_number.setText(previousHealth.numeroAplicaciones.toString())
        observations_health.setText(previousHealth.observaciones.toString())
    }

    private fun plusPage() {

        binding.page = binding.page!!.plus(1)

    }

    private fun minusPage() {
        binding.page = binding.page!!.minus(1)
    }



    private fun fechaProxima1(fecha: Date, aplicaciones: Int, frecuencia: Int): Date? {
       // unidadTiempo = frecuencyOptionsHealth.selectedItem.toString()
        fecha.addCurrentHour()
        return if (aplicaciones > 1) {
            when (unidadTiempo) {
                "Horas" -> fecha.add(Calendar.HOUR, frecuencia)
                "Días" -> fecha.add(Calendar.DATE, frecuencia)
                "Meses" -> fecha.add(Calendar.MONTH, frecuencia)
                else -> fecha.add(Calendar.YEAR, frecuencia)
            }
        } else {
            null
        }
    }

    companion object {
        const val PREVIOUS_HEALTH = "previousHealth"
    }

}