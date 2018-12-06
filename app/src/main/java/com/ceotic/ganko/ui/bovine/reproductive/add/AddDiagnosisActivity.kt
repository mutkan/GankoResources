package com.ceotic.ganko.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_120_EMPTY_DAYS
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_45_EMPTY_DAYS
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_60_EMPTY_DAYS
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_90_EMPTY_DAYS
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_BIRTH
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_DRYING
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_EMPTY_DAYS
import com.ceotic.ganko.data.models.Bovino.Companion.ALERT_PREPARATION
import com.ceotic.ganko.data.models.Diagnostico
import com.ceotic.ganko.data.models.Novedad
import com.ceotic.ganko.data.models.ReproductiveNotification
import com.ceotic.ganko.data.models.Servicio
import com.ceotic.ganko.databinding.ActivityAddDiagnosisBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_ID
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_POSITION
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_SERVICE
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_TYPE
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.TYPE_DIAGNOSIS
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.TYPE_NOVELTY
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.ceotic.ganko.util.*
import com.ceotic.ganko.work.NotificationWork
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_diagnosis.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddDiagnosisActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    val viewModel: ReproductiveBvnViewModel by lazy { buildViewModel<ReproductiveBvnViewModel>(factory) }
    private val idBovino: String by lazy { intent.getStringExtra(ARG_ID) ?: "" }
    private val type: Int by lazy { intent.getIntExtra(ARG_TYPE, 0) }
    private val servicio: Servicio by lazy { intent.getParcelableExtra(ARG_SERVICE) as Servicio }
    private val position: Int by lazy { intent.getIntExtra(ARG_POSITION, 0) }
    private val dis: LifeDisposable = LifeDisposable(this)
    lateinit var binding: ActivityAddDiagnosisBinding
    var posibleParto: Date? = null
    var diasVacios:Long? = null
    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private val datePicker: DatePickerDialog by lazy {
        DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_diagnosis)
        binding.type = type
        title = if (type == TYPE_DIAGNOSIS) "Agregar Diagnostico" else "Agregar Novedad"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        if (type == TYPE_DIAGNOSIS) {
            val servDate = servicio.fecha!!.time
            val posB = Calendar.getInstance().apply { timeInMillis = servDate }
            posB.add(Calendar.DATE, 285)
            posibleParto = Date(posB.timeInMillis)
            possibleBirth.text = posibleParto!!.toStringFormat()
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()

        if (type == TYPE_NOVELTY){
            dis add viewModel.getEmptyDaysForBovine(idBovino,servicio.fecha!!)
                    .subscribeBy(
                            onSuccess = {
                                diasVacios = it
                            }
                    )
        }


        dis add notPregnant.checkedChanges()
                .subscribeBy(
                        onNext = {
                            possibleBirth.visibility = if (it) View.INVISIBLE else View.VISIBLE
                            possibleBirthTxt.visibility = if (it) View.INVISIBLE else View.VISIBLE
                        }
                )

        dis add diagnosisDate.clicks()
                .subscribeBy(
                        onNext = {
                            datePicker.show()
                        }
                )

        dis add btnCancelDiagnosis.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )

        dis add btnAcceptDiagnosis.clicks()
                .flatMap { validateFields() }
                .flatMapMaybe {
                    val servicio = if (type == TYPE_DIAGNOSIS) setDiagnosis(it) else setNovedad(it)
                    val failed = !servicio.diagnostico!!.confirmacion && servicio.finalizado!!
                    Log.d("failed", failed.toString())
                    viewModel.updateServicio(idBovino, servicio, position, failed)
                }.flatMapSingle { bovino ->
                    val servicioActual = bovino.servicios!![position]
                    val fechaServicioActual = servicioActual.fecha!!.toStringFormat()
                    when {
                        type == TYPE_DIAGNOSIS && servicioActual.diagnostico?.confirmacion == true -> {
                            val posibleParto = servicioActual.posFechaParto!!
                            val dif = posibleParto.time - Date().time
                            val daysToBirth = TimeUnit.DAYS.convert((dif), TimeUnit.MILLISECONDS)
                            Log.d("dias para el parto", daysToBirth.toString())
                            val notifyTimeSecado = daysToBirth - 60
                            val notifyTimePreparacion = daysToBirth - 30
                            val notifyTimeParto = daysToBirth - 1

                            val uuid45EmptyDays = bovino.notificacionesReproductivo?.get(ALERT_45_EMPTY_DAYS)
                            val uuid60EmptyDays = bovino.notificacionesReproductivo?.get(ALERT_60_EMPTY_DAYS)
                            val uuid90EmptyDays = bovino.notificacionesReproductivo?.get(ALERT_90_EMPTY_DAYS)
                            val uuid120EmptyDays = bovino.notificacionesReproductivo?.get(ALERT_120_EMPTY_DAYS)

                            NotificationWork.cancelNotificationsById(uuid45EmptyDays, uuid60EmptyDays, uuid90EmptyDays, uuid120EmptyDays)

                            if (notifyTimeSecado >= 0) {
                                val uuidSecado = NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, "Recordatorio Secado", "Comenzar secado del bovino: ${bovino.nombre}, fecha del servicio: $fechaServicioActual, posible parto: ${posibleParto.toStringFormat()}", idBovino,
                                        notifyTimeSecado, TimeUnit.DAYS)
                                bovino.notificacionesReproductivo!![ALERT_DRYING] = uuidSecado
                            }
                            if (notifyTimePreparacion >= 0) {
                                val uuidPreparacion = NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, "Recordatorio Preparación", "Comenzar preparación para el parto del bovino: ${bovino.nombre}, fecha del servicio: $fechaServicioActual, posible parto: ${posibleParto.toStringFormat()}", idBovino,
                                        notifyTimePreparacion, TimeUnit.DAYS)
                                bovino.notificacionesReproductivo!![ALERT_PREPARATION] = uuidPreparacion
                            }
                            if (notifyTimeParto >= 0) {
                                val uuidParto = NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, "Recordatorio Parto", "Es probable que el parto del bovino: ${bovino.nombre} sea mañana, fecha del servicio: $fechaServicioActual, posible parto: ${posibleParto.toStringFormat()}", idBovino,
                                        notifyTimeParto, TimeUnit.DAYS)
                                bovino.notificacionesReproductivo!![ALERT_BIRTH] = uuidParto
                            }
                            viewModel.updateBovino(idBovino, bovino)
                                    .flatMap { viewModel.markNotifcationsAsAppliedByTagAndBovineId(ALERT_EMPTY_DAYS, idBovino) }

                        }
                        type == TYPE_NOVELTY && servicioActual.finalizado!! -> {
                            val uuidSecado = bovino.notificacionesReproductivo!![ALERT_DRYING]
                            val uuidPreparacion = bovino.notificacionesReproductivo!![ALERT_PREPARATION]
                            val uuidParto = bovino.notificacionesReproductivo!![ALERT_BIRTH]
                            NotificationWork.cancelNotificationsById(uuidSecado, uuidPreparacion, uuidParto)
                            val emptyDaysNotifications = ReproductiveNotification.setEmptyDaysNotifications(bovino,servicioActual.novedad!!.fecha)
                            if (bovino.serviciosFallidos!! == 3) {
                                val uuidServiciosFallidos = NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, "Tres Servicios Fallidos", "El bovino: ${bovino.nombre}, lleva 3 servicios fallidos de manera consecutiva", idBovino,
                                        10, TimeUnit.SECONDS)
                                Log.d("SERVICIOS FALLIDOS", uuidServiciosFallidos.toString())
                            }
                            viewModel.insertNotifications(emptyDaysNotifications).flatMap {
                                viewModel.updateBovino(idBovino, bovino)
                            }
                        }
                        else -> viewModel.updateBovino(idBovino, bovino)
                    }
                }
                .subscribeBy(
                        onNext = {

                            finish()
                        }
                )
    }

    private fun validateFields(): Observable<List<String>> {
        val fecha = diagnosisDate.text()
        return validateForm(R.string.empty_fields, fecha)
    }

    private fun setDiagnosis(params: List<String>): Servicio {
        val fecha = params[0].toDate()
        val confirmacion = pregnant.isChecked
        val diagnostico = Diagnostico(fecha, confirmacion)
        val fechaPosParto = if (confirmacion) posibleParto else null
        return servicio.apply {
            this.diagnostico = diagnostico
            this.posFechaParto = fechaPosParto
            this.finalizado = confirmacion.not()
        }
    }

    private fun setNovedad(params: List<String>): Servicio {
        val fecha = params[0].toDate()
        val novedad = noveltySpinner.selectedItem.toString()
        val mNovedad = Novedad(fecha, novedad, diasVacios)

        return servicio.apply {
            this.novedad = mNovedad
            this.finalizado = true
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        diagnosisDate.setText("$dayOfMonth/${month + 1}/$year")
    }
}
