package com.ceotic.ganko.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_ID
import com.ceotic.ganko.ui.bovine.reproductive.ListZealFragment.Companion.ARG_ZEAL
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.ceotic.ganko.util.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_service.*
import java.util.*
import javax.inject.Inject

class AddServiceActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ReproductiveBvnViewModel by lazy { buildViewModel<ReproductiveBvnViewModel>(factory) }
    private val idBovino: String by lazy { intent.getStringExtra(ARG_ID) ?: "" }
    private val celo: String by lazy { intent.getStringExtra(ARG_ZEAL) ?: "" }
    private val dis: LifeDisposable = LifeDisposable(this)
    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private val strawAdapter: ArrayAdapter<Straw> by lazy { ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, mutableListOf<Straw>()) }
    private val bullAdapter: ArrayAdapter<Bovino> by lazy { ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, mutableListOf<Bovino>()) }
    private val datePicker: DatePickerDialog by lazy {
        DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }
    private lateinit var bovino: Bovino

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_service)
        title = "Agregar Servicio"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getBovino(idBovino)
                .subscribeBy(
                        onSuccess = {
                            bovino = it
                        },
                        onComplete = {
                            Log.d("onCOmplete", "Oncomplete")
                        },
                        onError = {
                            Log.d("onError", "onError")
                        }
                )

        dis add viewModel.getAllBulls()
                .subscribeBy(
                        onSuccess = {
                            bullAdapter.clear()
                            bullAdapter.addAll(it)
                            bullAdapter.notifyDataSetChanged()
                        }
                )

        dis add viewModel.getAllStraws()
                .subscribeBy(
                        onSuccess = {
                            strawAdapter.clear()
                            strawAdapter.addAll(it)
                            strawAdapter.notifyDataSetChanged()
                        }
                )

        dis add serviceType.checkedChanges()
                .subscribeBy(
                        onNext = {
                            bullCodeOrStrawTxt.text = if (it == R.id.naturalMating) "Código del Toro" else "Pajilla"
                            bullCodeOrStrawSpinner.adapter = if (it == R.id.naturalMating) bullAdapter else strawAdapter
                        }
                )

        dis add serviceDate.clicks()
                .subscribeBy(
                        onNext = {
                            datePicker.show()
                        }
                )

        dis add btnAcceptService.clicks()
                .flatMap {
                    validateFields()
                }
                .flatMapSingle {
                    val servicio = createService(it)
                    if (serviceType.checkedRadioButtonId == R.id.naturalMating)
                        viewModel.insertService(idBovino, servicio)
                    else
                        viewModel.insertService(idBovino, servicio).flatMap { bovino ->
                            val pajilla = bullCodeOrStrawSpinner.selectedItem as Straw
                            viewModel.markStrawAsUsed(pajilla._id!!).map { bovino }
                        }
                }
                .flatMapSingle { bovino ->
                    val date = bovino.servicios!![0].fecha!!.time / 60000
                    val now = Date().time / 60000

                    val diagnosis = date + 48960
                    val dNotification = diagnosis - now

                    val zeal = date + 30240
                    val dZeal = zeal - now

                    val alarms: MutableList<Pair<Alarm, Long>> = mutableListOf()

                    if (dNotification + DAY_7_MIN > 0) {
                        alarms.add(Alarm(
                                bovino = AlarmBovine(bovino._id!!, bovino.nombre!!, bovino.codigo!!),
                                titulo = "Diagnostico de preñez",
                                descripcion = "Verificar estado de preñez",
                                alarma = ALARM_DIAGNOSIS,
                                fechaProxima = Date(diagnosis * 60000),
                                type = TYPE_ALARM,
                                activa = true,
                                reference = bovino._id
                        ) to dNotification)
                    }

                    if (dZeal + DAY_7_MIN > 0) {
                        alarms.add(Alarm(
                                bovino = AlarmBovine(bovino._id!!, bovino.nombre!!, bovino.codigo!!),
                                titulo = "21 Dias desde el servicio",
                                descripcion = "Verificar celo",
                                alarma = ALARM_ZEAL_21,
                                fechaProxima = Date(zeal * 60000),
                                type = TYPE_ALARM,
                                activa = true,
                                reference = bovino._id
                        ) to dZeal)
                    }
                    viewModel.cancelNotiByDiagnosis(bovino._id!!, ALARM_ZEAL_21
                            , ALARM_ZEAL_42, ALARM_ZEAL_64, ALARM_ZEAL_84, fromNow = false)
                            .flatMap { viewModel.insertNotifications(alarms) }


                }
                .subscribeBy(
                        onNext = {
                            finish()
                        },
                        onError = {
                            Log.e("ERROR AGREGAR SERVICO", it.message, it)
                        }
                )

        dis add btnCancelService.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )


    }

    private fun createService(params: List<String>): Servicio {
        val fecha = params[0].toDate()
                .addCurrentHour(10)
        val condicion = params[1].toDouble()
        val montaN = getString(R.string.monta_natural)
        val inseminacion = getString(R.string.inseminaci_n_artificial)
        val empadre = if (serviceType.checkedRadioButtonId == R.id.naturalMating) montaN else inseminacion
        val codigoToro = if (empadre == montaN) bullCodeOrStrawSpinner.selectedItem.toString() else null
        val pajilla = if (empadre == inseminacion) bullCodeOrStrawSpinner.selectedItem.toString() else null
        val fechaCelo: Date? = if (celo != "") celo.toDate() else null
        return Servicio(fecha, fechaCelo, condicion, empadre, codigoToro, pajilla, null, null, null, null, false)
    }

    private fun validateFields(): Observable<List<String>> {
        val fecha = serviceDate.text()
        val condicion = bodyCondition.text()
        return validateForm(R.string.empty_fields, fecha, condicion)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        serviceDate.setText("$dayOfMonth/${month + 1}/$year")
    }
}
