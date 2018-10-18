package com.example.cristian.myapplication.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Diagnostico
import com.example.cristian.myapplication.data.models.Novedad
import com.example.cristian.myapplication.data.models.Servicio
import com.example.cristian.myapplication.databinding.ActivityAddDiagnosisBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_ID
import com.example.cristian.myapplication.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_POSITION
import com.example.cristian.myapplication.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_SERVICE
import com.example.cristian.myapplication.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_TYPE
import com.example.cristian.myapplication.ui.bovine.reproductive.ListServiceFragment.Companion.TYPE_DIAGNOSIS
import com.example.cristian.myapplication.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.example.cristian.myapplication.util.*
import com.example.cristian.myapplication.work.NotificationWork
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
                    viewModel.updateServicio(idBovino, servicio, position)
                }
                .flatMapSingle { bovino ->
                    val servicioActual = bovino.servicios!![position]
                    val fechaServicioActual = servicioActual.fecha!!.toStringFormat()
                    return@flatMapSingle if (type == TYPE_DIAGNOSIS && servicioActual.diagnostico?.confirmacion == true) {
                        val posibleParto = servicioActual.posFechaParto!!
                        val dif = posibleParto.time - Date().time
                        val notifyTimeSecado = TimeUnit.DAYS.convert((dif - 60), TimeUnit.MILLISECONDS)
                        val notifyTimePreparacion = TimeUnit.DAYS.convert((dif - 30), TimeUnit.MILLISECONDS)
                        val notifyTimeParto = TimeUnit.DAYS.convert((dif - 1), TimeUnit.MILLISECONDS)
                        Single.just({
                            NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, "Recordatorio Secado", "Comenzar secado del bovino: ${bovino.nombre}, fecha del servicio: $fechaServicioActual, posible parto: ${posibleParto.toStringFormat()}", idBovino,
                                    notifyTimeSecado, TimeUnit.DAYS)
                            NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, "Recordatorio Preparación", "Comenzar preparación para el parto del bovino: ${bovino.nombre}, fecha del servicio: $fechaServicioActual, posible parto: ${posibleParto.toStringFormat()}", idBovino,
                                    notifyTimePreparacion, TimeUnit.DAYS)
                            NotificationWork.notify(NotificationWork.TYPE_REPRODUCTIVE, "Recordatorio Parto", "Es probable que el parto del bovino: ${bovino.nombre} sea mañana, fecha del servicio: $fechaServicioActual, posible parto: ${posibleParto.toStringFormat()}", idBovino,
                                    notifyTimeParto, TimeUnit.DAYS)
                        })
                    } else {
                        Single.just(Unit)
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
        val mNovedad = Novedad(fecha, novedad)

        return servicio.apply {
            this.novedad = mNovedad
            this.finalizado = true
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        diagnosisDate.setText("$dayOfMonth/${month + 1}/$year")
    }
}
