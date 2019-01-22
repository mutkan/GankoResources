package com.ceotic.ganko.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_21_DAYS_AFTER_SERVICE
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_DIAGNOSIS
import com.ceotic.ganko.data.models.Servicio
import com.ceotic.ganko.data.models.Straw
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_ID
import com.ceotic.ganko.ui.bovine.reproductive.ListZealFragment.Companion.ARG_ZEAL
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.ceotic.ganko.util.*
import com.ceotic.ganko.work.NotificationWork
import com.ceotic.ganko.work.NotificationWork.Companion.TYPE_REPRODUCTIVE
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_service.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddServiceActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ReproductiveBvnViewModel by lazy { buildViewModel<ReproductiveBvnViewModel>(factory) }
    private val idBovino: String by lazy { intent.getStringExtra(ARG_ID) ?: "" }
    private val celo: String by lazy { intent.getStringExtra(ARG_ZEAL) ?: ""}
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
                    val ultimoServicio = bovino.servicios!![0].fecha!!.toStringFormat()
                    val dif = Date().time - bovino.servicios!![0].fecha!!.time
                    val daysSinceService = TimeUnit.DAYS.convert((dif), TimeUnit.MILLISECONDS)
                    val daysToDiagnosis =  34 - daysSinceService
                    val daysToAlert = 21 - daysSinceService
                    Log.d("Dias para diagnostico", daysToDiagnosis.toString())
                    Log.d("Dias para alerta", daysToAlert.toString())
                    if(daysToDiagnosis >= 0){
                        val uuidDiagnosis = NotificationWork.notify(TYPE_REPRODUCTIVE, "Recordatorio Diagnostico de preñez", "Verificar preñez del bovino ${bovino.nombre}, fecha del servicio $ultimoServicio", idBovino,
                                daysToDiagnosis, TimeUnit.DAYS)
                        bovino.notificacionesReproductivo!![ALERT_DIAGNOSIS] = uuidDiagnosis
                    }
                    if(daysToAlert >= 0){
                        val uuid21Days = NotificationWork.notify(TYPE_REPRODUCTIVE, "Recordatorio 21 días desde el servicio", "Verificar celo del bovino ${bovino.nombre}, fecha del servicio $ultimoServicio", idBovino,
                                daysToAlert, TimeUnit.DAYS)
                        bovino.notificacionesReproductivo!![ALERT_21_DAYS_AFTER_SERVICE] = uuid21Days
                    }
                    viewModel.updateBovino(idBovino, bovino)
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
