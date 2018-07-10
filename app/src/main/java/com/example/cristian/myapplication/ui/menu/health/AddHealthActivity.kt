package com.example.cristian.myapplication.ui.menu.health

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.databinding.ActivityAddHealthBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.groups.GroupFragment
import com.example.cristian.myapplication.ui.groups.SelectActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.*
import com.example.cristian.myapplication.work.NotificationWork
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
    private val farmId by lazy { menuViewModel.getFarmId() }
    lateinit var datePicker: DatePickerDialog
    lateinit var binding: ActivityAddHealthBinding
    val currentDate : Date = Date()
    var group: Group? = null
    var bovines: List<String>? = null
    var groupFragment: GroupFragment? = null
    val unidades:Array<String> by lazy { resources.getStringArray(R.array.time_units) }
    var unidadTiempo:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_health)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Agregar sanidad")
        fixColor(8)
        title = getString(R.string.add_bovine)
        binding.page = 1
        datePicker = DatePickerDialog(this, AddHealthActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        onDateSet(null, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        startActivityForResult<SelectActivity>(SelectActivity.REQUEST_SELECT,
                SelectActivity.EXTRA_COLOR to 8)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SelectActivity.REQUEST_SELECT) {
            if(resultCode == Activity.RESULT_OK){
                group = data?.extras?.getParcelable(SelectActivity.DATA_GROUP)
                bovines = data?.extras?.getStringArray(SelectActivity.DATA_BOVINES)?.toList()
            }else{
                finish()
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

                }.subscribeBy (
                        onNext = {
                            plusPage()
                        }
                )

        dis add btnFinalizeHealth.clicks()
                .flatMap {

                    validateForm(R.string.empty_fields, dosis.text.toString(), frequency.text(),
                            product_value.text.toString(), attention_value.text.toString(), applicacion_number.text.toString(),
                            observations_health.text.toString())
                }

                .flatMapSingle {
                  //  NotificationWork.notify(0,"Sanidad",binding.diagnosis.text()
                    val frequencyTime = frequency.text().toLong()
                    val proxTime = when(unidadTiempo){
                        "Horas"-> frequencyTime
                        "Días" -> frequencyTime * 24
                        "Meses"-> frequencyTime * 24 * 30
                        else -> frequencyTime * 24 * 30 * 12
                    }
                    val notifyTime:Long = when(proxTime){
                        in 3..24 -> proxTime - 1
                        else -> proxTime - 24
                    }

                    viewModel.addHealth(
                            Sanidad(null, null, null, farmId, dateAddHealth.text.toString().toDate(),
                                    fechaProxima1( dateAddHealth.text().toDate(), applicacion_number.text().toInt(), frequency.text().toInt()),
                                    frequency.text().toInt(), spinnerEvent.selectedItem.toString(),
                                    if(binding.otherSelect) other.text() else null, diagnosis.text(), treatment_health.text(),
                                    product_health.text(), dosis.text(), null, applicacion_number.text().toInt(), 1,
                                    observations_health.text(), product_value.text().toInt(), attention_value.text().toInt(),
                                    null, bovines!! ,unidadTiempo,0, emptyList())
                    ).map { it to notifyTime }
                }
                .subscribeBy(
                        onNext = {
                            NotificationWork.notify(NotificationWork.TYPE_HEALTH,"Sanidad", diagnosis.text(),it.first,
                                    it.second,TimeUnit.HOURS)
                            toast("Sanidad agregada exitosamente")
                            finish() },

                        onComplete = {
                            toast("onComplete")
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )
    }

    private fun plusPage() {

        binding.page = binding.page!!.plus(1)

    }

    private fun minusPage() {
        binding.page = binding.page!!.minus(1)
    }



    private fun fechaProxima1( fecha:Date, aplicaciones: Int, frecuencia: Int): Date{
        unidadTiempo = unidades[frecuencyOptionsHealth.selectedItemPosition]
        if (aplicaciones != 0) {
            when (unidadTiempo) {
                "Horas" -> fecha.add(Calendar.HOUR, frecuencia)
                "Días" -> fecha.add(Calendar.DATE, frecuencia)
                "Meses" -> fecha.add(Calendar.MONTH, frecuencia)
                else -> fecha.add(Calendar.YEAR, frecuencia)
            }
        }else{
            null
        }
        return fecha
    }


}
