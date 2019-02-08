package com.ceotic.ganko.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.bovine.reproductive.ListZealFragment.Companion.ID_BOVINO
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.ceotic.ganko.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_zeal.*
import java.util.*
import javax.inject.Inject

class AddZealActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ReproductiveBvnViewModel by lazy { buildViewModel<ReproductiveBvnViewModel>(factory) }
    val idBovino: String by lazy { intent.getStringExtra(ID_BOVINO) ?: "" }
    val dis: LifeDisposable = LifeDisposable(this)
    private var nextZealDate: Date = Date()
    val calendar: Calendar by lazy { Calendar.getInstance() }
    val datePicker: DatePickerDialog by lazy {
        DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_zeal)
        supportActionBar?.title = "Registrar Celo"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        dis add btnAddZeal.clicks()
                .flatMap { validateForm(R.string.empty_fields, zealDate.text()) }
                .flatMapMaybe {
                    val date = it[0].toDate()
                            .addCurrentHour()
                    viewModel.insertZeal(idBovino, date, nextZealDate)
                }
                .flatMapSingle { bovino ->
                    val dif = (nextZealDate.time / 60000) - (Date().time / 60000)

                    if (dif >= 0) {
                        val not = Alarm(
                                bovino = AlarmBovine(bovino._id!!, bovino.nombre!!, bovino.codigo!!),
                                titulo = "Recordatorio Celo",
                                descripcion = "Posible que el bovino ${bovino.codigo} entre en celo",
                                alarma = ALARM_ZEAL_21,
                                fechaProxima = nextZealDate,
                                type = TYPE_ALARM,
                                activa = true,
                                reference = bovino._id
                        ) to dif

                        viewModel.cancelNotiByDiagnosis(bovino._id!!, ALARM_ZEAL_21, ALARM_ZEAL_42, ALARM_ZEAL_64,
                                ALARM_ZEAL_84)
                                .flatMap { viewModel.insertNotifications(listOf(not)) }


                    } else {
                        viewModel.cancelNotiByDiagnosis(bovino._id!!, ALARM_ZEAL_21, ALARM_ZEAL_42, ALARM_ZEAL_64,
                                ALARM_ZEAL_84)
                                .flatMap { Single.just(listOf("")) }

                    }


                }
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )

        dis add btnCancelZeal.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )
        dis add zealDate.clicks()
                .subscribeBy(
                        onNext = {
                            datePicker.apply {
                                updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH))
                                datePicker.tag = "zealDate"
                                show()
                            }
                        }
                )

    }

    private fun calculateNextZeal(year: Int, month: Int, dayOfMonth: Int) {
        val zealDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        val nextZeal = zealDate.apply { add(Calendar.DATE, 21) }
        nextZealDate = nextZeal.time.addCurrentHour(3)
        possibleZealDate.text = nextZealDate.toStringFormat()
        possibleZealDate.visibility = View.VISIBLE
        possibleZealDateTxt.visibility = View.VISIBLE
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val sDate = "$dayOfMonth/${month + 1}/$year"
        view?.let {
            when (it.tag) {
                "zealDate" -> {
                    zealDate.setText(sDate)
                    calculateNextZeal(year, month, dayOfMonth)
                }
            }
        }
    }

}
