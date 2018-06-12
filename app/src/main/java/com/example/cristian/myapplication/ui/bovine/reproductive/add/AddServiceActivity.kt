package com.example.cristian.myapplication.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.data.models.Servicio
import com.example.cristian.myapplication.data.models.Straw
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_ID
import com.example.cristian.myapplication.ui.bovine.reproductive.ListZealFragment
import com.example.cristian.myapplication.ui.bovine.reproductive.ListZealFragment.Companion.ARG_ZEAL
import com.example.cristian.myapplication.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checked
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_service.*
import java.util.*
import javax.inject.Inject

class AddServiceActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ReproductiveBvnViewModel by lazy { buildViewModel<ReproductiveBvnViewModel>(factory) }
    private val idBovino: String by lazy { intent.getStringExtra(ARG_ID) ?: "" }
    private val celo: Date by lazy { intent.getStringExtra(ARG_ZEAL).toDate() }
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

        dis add date.clicks()
                .subscribeBy(
                        onNext = {
                            datePicker.show()
                        }
                )

        dis add btnAccept.clicks()
                .flatMap {
                    validateFields()
                }
                .flatMapSingle {
                    createService(it)
                }.flatMapSingle { viewModel.insertService(idBovino, it) }
                .subscribeBy(
                        onNext = {
                            finish()
                        },
                        onError = {
                            Log.e("ERROR AGREGAR SERVICO", it.message, it)
                        }
                )

        dis add btnCancel.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )


    }

    private fun createService(params: List<String>): Single<Servicio> {
        val fecha = params[0].toDate()
        val condicion = params[1].toDouble()
        val montaN = getString(R.string.monta_natural)
        val inseminacion = getString(R.string.inseminaci_n_artificial)
        val empadre = if (serviceType.checkedRadioButtonId == R.id.naturalMating) montaN else inseminacion
        val codigoToro = if (empadre == montaN) bullCodeOrStrawSpinner.selectedItem.toString() else null
        val pajilla = if (empadre == inseminacion) bullCodeOrStrawSpinner.selectedItem.toString() else null
        val servicio = Servicio(fecha, celo, condicion, empadre, codigoToro, pajilla, null, null, null, null, false)
        return Single.just(servicio)
    }

    private fun validateFields(): Observable<List<String>> {
        val fecha = date.text()
        val condicion = bodyCondition.text()
        return validateForm(R.string.empty_fields, fecha, condicion)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        date.setText("$dayOfMonth/${month + 1}/$year")
    }
}
