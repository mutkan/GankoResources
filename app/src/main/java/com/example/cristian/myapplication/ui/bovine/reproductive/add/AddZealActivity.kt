package com.example.cristian.myapplication.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.bovine.reproductive.ListZealFragment.Companion.ID_BOVINO
import com.example.cristian.myapplication.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_zeal.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class AddZealActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ReproductiveBvnViewModel by lazy { buildViewModel<ReproductiveBvnViewModel>(factory) }
    val idBovino: String by lazy { intent.getStringExtra(ID_BOVINO) ?: "" }
    val dis: LifeDisposable = LifeDisposable(this)
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
        dis add btnAdd.clicks()
                .flatMap { validateForm(R.string.empty_fields, possibleZealDate.text(), zealDate.text()) }
                .flatMapSingle { validateDates(it[0].toDate(), it[1].toDate()) }
                .flatMapMaybe {
                    Log.d("IDBOVINO", idBovino)
                    viewModel.insertZeal(idBovino, it.first, it.second)
                }
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )

        dis add btnCancel.clicks()
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
        dis add possibleZealDate.clicks()
                .subscribeBy(
                        onNext = {
                            datePicker.apply {
                                updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH))
                                datePicker.tag = "possibleZealDate"
                                show()
                            }
                        }
                )

    }

    private fun validateDates(possibleZealDate: Date, zealDate: Date): Single<Pair<Date, Date>> = Single.create { emitter ->
        if (possibleZealDate.after(zealDate))
            emitter.onSuccess(Pair(zealDate, possibleZealDate))
        else
            toast("Fechas no validas")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val sDate = "$dayOfMonth/${month + 1}/$year"
        view?.let {
            when (it.tag) {
                "zealDate" -> {
                    zealDate.setText(sDate)
                }
                "possibleZealDate" -> {
                    possibleZealDate.setText(sDate)
                }
            }
        }
    }

}
