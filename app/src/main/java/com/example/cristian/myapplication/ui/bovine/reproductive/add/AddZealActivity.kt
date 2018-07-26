package com.example.cristian.myapplication.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.bovine.reproductive.ListZealFragment.Companion.ID_BOVINO
import com.example.cristian.myapplication.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.example.cristian.myapplication.util.*
import com.example.cristian.myapplication.work.NotificationWork
import com.example.cristian.myapplication.work.NotificationWork.Companion.TYPE_REPRODUCTIVE
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_zeal.*
import java.util.*
import java.util.concurrent.TimeUnit
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
                    Log.d("IDBOVINO", idBovino)
                    viewModel.insertZeal(idBovino, it[0].toDate(), nextZealDate)
                }
                .flatMapSingle { bovino ->
                    val ultimoCelo = bovino.celos!![0].toStringFormat()
                    val dif = nextZealDate.time - Date().time
                    val notifyTime = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS) - 1
                    Single.just(NotificationWork.notify(TYPE_REPRODUCTIVE, "Recordatorio Celo", "Es probable que el bovino ${bovino.nombre} entre en celo mañana, fecha de ultimo celo $ultimoCelo", idBovino,
                            notifyTime, TimeUnit.DAYS))

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
        nextZealDate = nextZeal.time
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
