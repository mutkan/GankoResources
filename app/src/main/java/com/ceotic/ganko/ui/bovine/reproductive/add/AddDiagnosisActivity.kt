package com.ceotic.ganko.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.*
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
import io.reactivex.Single
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
    var diasVacios: Long? = null
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


        dis add viewModel.getEmptyDaysForBovine(idBovino, servicio.fecha!!)
                .subscribeBy(
                        onSuccess = {
                            diasVacios = it
                        }
                )



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
                    viewModel.updateServicio(idBovino, servicio, position, failed)
                }.flatMapSingle { bovino ->
                    val servicioActual = bovino.servicios!![position]

                    when {
                        type == TYPE_DIAGNOSIS && servicioActual.diagnostico?.confirmacion == true ->
                            prepareConfirmedDiagnosis(bovino, servicioActual)
                        type == TYPE_DIAGNOSIS && servicioActual.diagnostico?.confirmacion == false ->
                            prepareRejectedDiagnosis(bovino)
                        type == TYPE_NOVELTY && servicioActual.finalizado!! ->
                            viewModel.prepareNovelty(bovino, servicioActual)
                                    .flatMap {
                                        viewModel.prepareBirthZeal(bovino, servicioActual.novedad!!.fecha)
                                    }
                        else -> Single.just(emptyList())
                    }
                }
                .subscribeBy(
                        onNext = { finish() }
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
        val mDiasVacios = if (confirmacion) diasVacios else null
        val fechaPosParto = if (confirmacion) posibleParto else null
        return servicio.apply {
            this.diagnostico = diagnostico
            this.diasVacios = mDiasVacios?.toFloat()
            this.posFechaParto = fechaPosParto
            this.finalizado = confirmacion.not()
        }
    }

    private fun setNovedad(params: List<String>): Servicio {
        val fecha = params[0].toDate()
                .addCurrentHour(5)
        val novedad = noveltySpinner.selectedItem.toString()
        val mNovedad = Novedad(fecha, novedad)

        return servicio.apply {
            this.novedad = mNovedad
            this.finalizado = true
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        diagnosisDate.setText("$dayOfMonth/${month + 1}/$year")
    }

    fun prepareConfirmedDiagnosis(bovino: Bovino, service: Servicio): Single<List<Unit>> {
        val posibleParto = service.posFechaParto!!
        val now = Date().time
        val dif = posibleParto.time - now
        val minsToBirth = TimeUnit.MINUTES.convert((dif), TimeUnit.MILLISECONDS)
        val notifyTimeSecado = minsToBirth - 86400
        val notifyTimePreparacion = minsToBirth - 43200
        val notifyTimeParto = minsToBirth - 1440

        val alarms: MutableList<Pair<Alarm, Long>> = mutableListOf()

        if (notifyTimeSecado >= 0) {
            alarms.add(
                    Alarm(
                            bovino = AlarmBovine(bovino._id!!, bovino.nombre!!, bovino.codigo!!),
                            titulo = "Recordatorio Secado",
                            descripcion = "Comenzar secado",
                            alarma = ALARM_SECADO,
                            fechaProxima = Date(now + (notifyTimeSecado * 60000)),
                            type = TYPE_ALARM,
                            activa = true,
                            reference = bovino._id
                    ) to notifyTimeSecado
            )
        }

        if (notifyTimePreparacion >= 0) {
            alarms.add(
                    Alarm(
                            bovino = AlarmBovine(bovino._id!!, bovino.nombre!!, bovino.codigo!!),
                            titulo = "Recordatorio Preparación",
                            descripcion = "Comenzar preparación para el parto",
                            alarma = ALARM_PREPARACION,
                            fechaProxima = Date(now + (notifyTimePreparacion * 60000)),
                            type = TYPE_ALARM,
                            activa = true,
                            reference = bovino._id
                    ) to notifyTimePreparacion
            )
        }

        if (notifyTimeParto >= 0) {
            alarms.add(
                    Alarm(
                            bovino = AlarmBovine(bovino._id!!, bovino.nombre!!, bovino.codigo!!),
                            titulo = "Recordatorio Parto",
                            descripcion = "Posible parto el dia de mañana",
                            alarma = ALARM_NACIMIENTO,
                            fechaProxima = Date(now + (notifyTimeParto * 60000)),
                            type = TYPE_ALARM,
                            activa = true,
                            reference = bovino._id
                    ) to notifyTimeParto
            )
        }

        return viewModel.insertNotifications(alarms)
                .flatMap { viewModel.cancelNotiByDiagnosis(bovino._id!!, ALARM_18_MONTHS,
                        ALARM_EMPTY_DAYS_45, ALARM_EMPTY_DAYS_60, ALARM_EMPTY_DAYS_90,
                        ALARM_EMPTY_DAYS_120, ALARM_ZEAL_21) }
    }

    fun prepareRejectedDiagnosis(bovino: Bovino): Single<List<Unit>> =
        if (bovino.serviciosFallidos!! == 3) {
            val uuid = NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, "Tres Servicios Fallidos", "El bovino: ${bovino.nombre}, lleva 3 servicios fallidos de manera consecutiva", idBovino,
                    2, TimeUnit.SECONDS)

            val alarm = Alarm(
                    bovino = AlarmBovine(bovino._id!!, bovino.nombre!!, bovino.codigo!!),
                    titulo = "Tres Servicios Fallidos",
                    descripcion = "El Bovino lleva 3 servicios fallidos de manera consecutiva",
                    alarma = ALARM_REJECT_DIAGNOSIS_3,
                    fechaProxima = Date(),
                    type = TYPE_ALARM,
                    activa = true,
                    reference = bovino._id
            )

            viewModel.insertAlarm(alarm, uuid)
                    .map { emptyList<Unit>() }

        }else{
            Single.just(emptyList())
        }






}
