package com.ceotic.ganko.ui.menu.management

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Group
import com.ceotic.ganko.data.models.ProxStates.Companion.APPLIED
import com.ceotic.ganko.data.models.RegistroManejo
import com.ceotic.ganko.data.models.RegistroManejo.Companion.NOT_APPLIED
import com.ceotic.ganko.databinding.ActivityAddManageBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.groups.GroupFragment
import com.ceotic.ganko.ui.groups.ReApplyActivity
import com.ceotic.ganko.ui.groups.ReApplyActivity.Companion.EXTRA_ID
import com.ceotic.ganko.ui.groups.ReApplyActivity.Companion.REQUEST_CODE
import com.ceotic.ganko.ui.groups.SelectActivity
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.*
import com.ceotic.ganko.work.NotificationWork
import com.ceotic.ganko.work.NotificationWork.Companion.TYPE_MANAGEMENT
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.internal.operators.single.SingleFromCallable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_manage.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddManageActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val edit: Boolean by lazy { intent.getBooleanExtra("edit", false) }
    lateinit var previousManage: RegistroManejo
    lateinit var datePicker: DatePickerDialog
    lateinit var binding: ActivityAddManageBinding

    private val farmId by lazy { viewModel.getFarmId() }

    var dia: Int = 0
    var mes: Int = 0
    var año: Int = 0

    var groupFragment: GroupFragment? = null
    var group: Group? = null
    var bovines: List<String>? = null
    var noBovines: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_manage)
        binding.edit = edit
        fixColor(5)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Agregar Manejo")
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        binding.page = 1
        binding.other = false
        datePicker = DatePickerDialog(this, AddManageActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        if (edit) {
            previousManage = intent!!.getParcelableExtra(PREVIOUS_MANAGE)
            startActivityForResult<ReApplyActivity>(REQUEST_CODE, EXTRA_ID to previousManage._id!!)
            setEdit()
        } else {
            startActivityForResult<SelectActivity>(SelectActivity.REQUEST_SELECT,
                    SelectActivity.EXTRA_COLOR to 5)
        }
    }

    override fun onResume() {
        super.onResume()
        setupGroupFragment()

        dis add btnSaveManage.clicks()
                .flatMap {
                    if (numberAplications.text().toInt() > 1) validateForm(R.string.add_frecuency_of_prox_application, frecuency.text())
                    else Observable.just("")
                }
                .flatMapSingle {
                    val manage = createManage()
                    if (edit) viewModel.insertManage(manage).flatMap { id ->
                        viewModel.updateManage(previousManage.apply { estadoProximo = APPLIED }).map { id }
                    }
                    else viewModel.insertManage(manage)
                }
                .flatMapSingle { docId ->
                    setNotification(docId)
                }.retry()
                .subscribeBy(
                        onNext = {
                            if (!edit) toast("Evento registrado")
                            finish()
                        },
                        onError = {
                            Log.e("ERROR", it.message, it)
                        }
                )

        dis add spinnerEventType.itemSelections()
                .subscribeBy(
                        onNext = {
                            binding.other = it == 6
                        }
                )



        dis add btnNextManage.clicks()
                .flatMap {
                    validateForm(R.string.empty_fields,
                            when {
                                spinnerEventType.selectedItem == "Corte de ombligo" -> "Corte de ombligo"
                                spinnerEventType.selectedItem == "Identificación" -> "Identificación"
                                spinnerEventType.selectedItem == "Descorne" -> "Descorne"
                                spinnerEventType.selectedItem == "Arreglo de cascos" -> "Arreglo de cascos"
                                spinnerEventType.selectedItem == "Castración" -> "Castración"
                                spinnerEventType.selectedItem == "Secado" -> "Secado"
                                else -> "Otro"
                            },
                            if (spinnerEventType.selectedItem == "Otro") otherWhich.text.toString()
                            else "No Other",
                            eventDate.text.toString(), treatment.text.toString(), numberAplications.text.toString()
                    )
                }
                .subscribeBy(
                        onNext = {
                            plusPage()
                        }
                )

        dis add btnBackManage.clicks()
                .subscribeBy(
                        onNext = {
                            minusPage()
                        }
                )

        dis add btnCancelManage.clicks()
                .subscribeByAction(
                        onNext = { finish() },
                        onHttpError = { toast(it) },
                        onError = { toast(it.message!!) }
                )

        dis add eventDate.clicks()
                .subscribe { datePicker.show() }
    }

    private fun setNotification(docId: String): Single<Unit>? = SingleFromCallable {
        var aplicaciones = numberAplications.text().toInt()
        if (aplicaciones > 1) {
            val proximaAplicacion = frecuency.text().toLong()
            val unidadTiempo = spinnerFrecuency.selectedItem.toString()
            val proxTime = when (unidadTiempo) {
                "Horas" -> proximaAplicacion
                "Días" -> proximaAplicacion * 24
                "Meses" -> proximaAplicacion * 24 * 30
                else -> proximaAplicacion * 24 * 30 * 12
            }
            val notifyTime: Long = when (proxTime) {
                in 3..24 -> proxTime - 1
                else -> proxTime - 24
            }
            val evento = spinnerEventType.selectedItem.toString()

            NotificationWork.notify(TYPE_MANAGEMENT, "Recordatorio Manejo", "Evento pendiente: $evento", docId,
                    notifyTime, TimeUnit.HOURS)
        }
    }

    private fun createManage(): RegistroManejo {
        val producto = product.text()
        val frecuencia = if (frecuency.text() == "") 0 else frecuency.text().toInt()
        val unidadTiempo = spinnerFrecuency.selectedItem.toString()
        val precioProducto = if (productPrice.text() == "") 0 else productPrice.text().toInt()
        val observaciones: String? = observations.text.toString()
        val precioAsistencia = if (assistancePrice.text() == "") 0 else assistancePrice.text().toInt()
        val fechaEvento = eventDate.text.toString().toDate()
        val evento = spinnerEventType.selectedItem.toString()
        var otro: String? = null
        if (evento == "Otro") otro = otherWhich.text.toString()
        val tratamiento = treatment.text.toString()
        val aplicaciones = numberAplications.text.toString().toInt()
        val fechaProximo = if (aplicaciones > 1) {
            when (unidadTiempo) {
                "Horas" -> fechaEvento.add(Calendar.HOUR, frecuencia)
                "Días" -> fechaEvento.add(Calendar.DATE, frecuencia)
                "Meses" -> fechaEvento.add(Calendar.MONTH, frecuencia)
                else -> fechaEvento.add(Calendar.YEAR, frecuencia)
            }
        } else null


        val numAplicacion = if (edit) {
            if (previousManage.numeroAplicaciones!! > previousManage.aplicacion!!) {
                previousManage.aplicacion!!.plus(1)
            } else {
                previousManage.aplicacion!!
            }
        } else 1

        return if (!edit) {
            RegistroManejo(idFinca = farmId, fecha = fechaEvento, fechaProxima = fechaProximo, frecuencia = frecuencia, unidadFrecuencia = unidadTiempo, numeroAplicaciones = aplicaciones,
                    aplicacion = 1, tipo = evento, titulo = evento, otro = otro, tratamiento = tratamiento, descripcion = tratamiento, producto = producto, observaciones = observaciones,
                    valorProducto = precioProducto, valorAsistencia = precioAsistencia, grupo = group, bovinos = bovines, estadoProximo = NOT_APPLIED)
        } else {
            RegistroManejo(idAplicacionUno = previousManage.idAplicacionUno, idFinca = farmId, fecha = fechaEvento, fechaProxima = if (aplicaciones > numAplicacion) {
                fechaProximo
            } else {
                null
            }, frecuencia = frecuencia, unidadFrecuencia = unidadTiempo, numeroAplicaciones = aplicaciones,
                    aplicacion = numAplicacion, tipo = evento, titulo = evento, otro = otro, tratamiento = tratamiento, descripcion = tratamiento, producto = producto, observaciones = observaciones,
                    valorProducto = precioProducto, valorAsistencia = precioAsistencia, grupo = group, bovinos = bovines, estadoProximo = NOT_APPLIED, noBovinos = noBovines)
        }

    }

    private fun setupGroupFragment() {
        if ((group != null || bovines != null) && groupFragment == null) {
            groupFragment = if (group != null) GroupFragment.instance(5, group!!)
            else GroupFragment.instance(5, bovines!!)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.manageContainer, groupFragment)
                    .commit()

            dis add groupFragment!!.ids
                    .filter { group == null }
                    .subscribe { bovines = it }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        eventDate.text = "$dayOfMonth/${month + 1}/$year"
        dia = dayOfMonth
        mes = month
        año = year
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

    private fun setEdit() {
        val unidades = resources.getStringArray(R.array.time_units)
        val tipoEvento = resources.getStringArray(R.array.event_type)
        val eventType = tipoEvento.indexOf(previousManage.tipo)
        val timeUnit = unidades.indexOf(previousManage.unidadFrecuencia)
        spinnerEventType.setSelection(eventType)
        spinnerEventType.isEnabled = false
        spinnerFrecuency.setSelection(timeUnit)
        spinnerFrecuency.isEnabled = false
        if (eventType == 6) otherWhich.setText(previousManage.otro)
        treatment.setText(previousManage.tratamiento)
        numberAplications.setText(previousManage.numeroAplicaciones!!.toString())
        product.setText(previousManage.producto)
        frecuency.setText(previousManage.frecuencia.toString())
        productPrice.setText(previousManage.valorProducto.toString())
        observations.setText(previousManage.observaciones)
        assistancePrice.setText(previousManage.valorAsistencia.toString())


    }


    private fun plusPage() {
        binding.page = binding.page!!.plus(1)
    }

    private fun minusPage() {
        binding.page = binding.page!!.minus(1)
    }

    companion object {
        const val PREVIOUS_MANAGE = "previousManage"
    }


}
