package com.example.cristian.myapplication.ui.manage

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.models.RegistroManejo
import com.example.cristian.myapplication.data.models.RegistroManejo.Companion.NOT_APPLIED
import com.example.cristian.myapplication.databinding.ActivityAddManageBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.groups.GroupFragment
import com.example.cristian.myapplication.ui.groups.SelectActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_manage.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Agregar Manejo")
        binding.page = 1
        binding.other = false
        datePicker = DatePickerDialog(this, AddManageActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        startActivityForResult<SelectActivity>(SelectActivity.REQUEST_SELECT,
                SelectActivity.EXTRA_COLOR to 5)
    }

    override fun onResume() {
        super.onResume()
        setupGroupFragment()

        dis add spinnerEventType.itemSelections()
                .subscribeBy(
                        onNext = {
                            binding.other = it == 6
                        }
                )

        dis add btnSaveManage.clicks()
                .flatMap {
                    validateForm(R.string.empty_fields, product.text.toString(), frecuency.text.toString(), productPrice.text.toString(),
                            observations.text.toString(), assistancePrice.text.toString())
                }
                .flatMapSingle {
                    val manage = createManage(it)
                    viewModel.insertManage(manage)
                }
                .subscribeBy(
                        onNext = {
                            toast("Evento registrado")
                            finish()
                        },
                        onError = {
                            toast(it.message!!)
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

    private fun createManage(fields: List<String>): RegistroManejo {
        val producto = fields[0]
        val frecuencia = fields[1].toInt()
        val unidadTiempo = spinnerFrecuency.selectedItem.toString()
        val precioProducto = fields[2].toInt()
        val observaciones = fields[3]
        val precioAsistencia = fields[4].toInt()
        val fechaEvento = eventDate.text.toString().toDate()
        val evento = spinnerEventType.selectedItem.toString()
        var otro: String? = null
        if (evento == "Otro") {
            otro = otherWhich.text.toString()
        }
        val tratamiento = treatment.text.toString()
        val aplicaciones = numberAplications.text.toString().toInt()
        var fechaProximo = if (aplicaciones != 0) {
            when (unidadTiempo) {
                "Horas" -> fechaEvento.add(Calendar.HOUR, frecuencia)
                "Días" -> fechaEvento.add(Calendar.DATE, frecuencia)
                "Meses" -> fechaEvento.add(Calendar.MONTH, frecuencia)
                else -> fechaEvento.add(Calendar.YEAR, frecuencia)
            }
        }else {
            null
        }


        return RegistroManejo(idFinca = farmId, fecha = fechaEvento, fechaProx = fechaProximo, frecuencia = frecuencia, numeroAplicaciones = aplicaciones,
                aplicacion = 1, tipo = evento, otro = otro, tratamiento = tratamiento, producto = producto, observaciones = observaciones,
                valorProducto = precioProducto, valorAsistencia = precioAsistencia, grupo = group, bovinos = bovines, estadoProximaAplicacion = NOT_APPLIED)

    }

    fun setupGroupFragment() {
        if ((group != null || bovines != null) && groupFragment == null) {
            groupFragment = if (group != null) GroupFragment.instance(12, group!!)
            else GroupFragment.instance(12, bovines!!)

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
        if (requestCode == SelectActivity.REQUEST_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                group = data?.extras?.getParcelable(SelectActivity.DATA_GROUP)
                bovines = data?.extras?.getStringArray(SelectActivity.DATA_BOVINES)?.toList()

            } else {
                finish()
            }
        }
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
