package com.ceotic.ganko.ui.menu.reports


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.models.Promedio
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.text
import com.ceotic.ganko.util.toDate
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.itemSelections
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_select_average.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.util.*
import javax.inject.Inject

class SelectAverageFragment : Fragment(), Injectable, com.borax12.materialdaterangepicker.date.DatePickerDialog.OnDateSetListener, android.app.DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    private val dis: LifeDisposable = LifeDisposable(this)
    val bovinesAdapter: ArrayAdapter<Bovino> by lazy { ArrayAdapter(this.context, R.layout.support_simple_spinner_dropdown_item, mutableListOf<Bovino>()) }
    val averageTypes: Array<String> by lazy { resources.getStringArray(R.array.average_types) }
    val sexSubject: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
    val idFinca: String by lazy { viewModel.getFarmId() }
    var tipo: Int = -1
    var fechaInit: Date? = null
    var fechaEnd: Date? = null
    var fechaS: Date? = null
    val calendar: Calendar by lazy { Calendar.getInstance() }
    var alimento: String = ""
    val tipo_alimento: Array<String> by lazy { resources.getStringArray(R.array.feed_types) }
    val year: Int by lazy { calendar.get(Calendar.YEAR) }
    val datePicker: DatePickerDialog by lazy {
        DatePickerDialog.newInstance(this, year, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }
    val datePickerAge: android.app.DatePickerDialog by lazy {
        android.app.DatePickerDialog(context, this,
                year,
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_average, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        spinnerBovine.adapter = bovinesAdapter
    }

    override fun onResume() {
        super.onResume()

        dis add sexSubject.flatMapSingle {
            if (it == 0) viewModel.getAllCows() else viewModel.getBovine(idFinca)
        }.subscribeBy(
                onNext = {
                    bovinesAdapter.clear()
                    bovinesAdapter.addAll(it)
                }
        )

        dis add averageType.itemSelections()
                .subscribeBy(
                        onNext = {
                            onAverageTypeChanged(it)
                        }
                )

        dis add food_type.itemSelections()
                .subscribeBy(
                        onNext = {
                            alimento = tipo_alimento[it]
                        }
                )

        dis add individualRadioButton.checkedChanges()
                .subscribeBy(
                        onNext = {
                            groupBov.visibility = if (it && groupTotOrInd.visibility == View.VISIBLE) View.VISIBLE else View.GONE
                        }
                )

        dis add rankRadioButton.checkedChanges()
                .subscribeBy(
                        onNext = {
                            when {
                                it && groupMonthOrRank.visibility == View.VISIBLE -> {
                                    groupDates.visibility = View.VISIBLE
                                    groupMonth.visibility = View.GONE
                                }
                                !it && groupMonthOrRank.visibility == View.VISIBLE -> {
                                    groupDates.visibility = View.GONE
                                    groupMonth.visibility = View.VISIBLE
                                }
                                tipo >= 6 -> {
                                    groupDates.visibility = View.VISIBLE
                                }
                            }
                        }
                )

        dis add fromDateText.clicks()
                .subscribeBy {
                    datePicker.show(activity!!.fragmentManager, "dialog")
                }

        dis add date.clicks()
                .subscribeBy {
                    datePickerAge.show()
                }

        dis add fabView.clicks()
                .flatMapSingle { validateRankAndIndividualAverages() }
                .flatMapMaybe { getRankAndIndividualAverages(tipo) }
                .doOnError {
                    Log.e("Error Promedios", it.message, it)
                    toast("Error obteniendo promedio: ${it.localizedMessage}")
                }
                .retry()
                .subscribeBy(
                        onNext = {
                            startActivity<AverageActivity>("promedio" to it)
                        }
                )
    }


    private fun validateRankAndIndividualAverages(): Single<Boolean> = Single.create { e ->
        val individualOk = if (individualRadioButton.isChecked && groupTotOrInd.visibility == View.VISIBLE) !bovinesAdapter.isEmpty else true
        val rankOk = if ((rankRadioButton.isChecked && groupMonthOrRank.visibility == View.VISIBLE) || tipo == 3) fromDateText.text() != "" else true
        val dateOk = if (tipo == 4) date.text() != "" else true
        when {
            individualOk && rankOk && dateOk -> e.onSuccess(true)
            !individualOk -> toast("No hay bovinos")
            else -> toast("Verifica los campos")
        }
    }

    private fun getRankAndIndividualAverages(averageType: Int): Maybe<Promedio> {
        val bovino: Bovino? = if (individualRadioButton.isChecked) spinnerBovine.selectedItem as Bovino else null
        val mes = spinnerMonth.selectedItemPosition
        val anio = year
        return when {
            averageType == 0 && individualRadioButton.isChecked && rankRadioButton.isChecked -> viewModel.averages.promedioLecheTotalYBovino(bovino!!._id!!, from = fechaInit!!, to = fechaEnd!!)
            averageType == 0 && individualRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.promedioLecheTotalYBovino(bovino!!._id!!, mes = mes, anio = anio)
            averageType == 0 && totalRadioButton.isChecked && rankRadioButton.isChecked -> viewModel.averages.getPromedioLeche(from = fechaInit!!, to = fechaEnd!!)
            averageType == 0 && totalRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.getPromedioLeche(mes = mes, anio = anio)


            averageType == 1 && individualRadioButton.isChecked && rankRadioButton.isChecked -> viewModel.averages.promedioGDPTotalYBovino(bovino!!._id!!, from = fechaInit!!, to = fechaEnd!!).toMaybe()
            averageType == 1 && individualRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.promedioGDPTotalYBovino(bovino!!._id!!, mes = mes, anio = anio).toMaybe()
            averageType == 1 && totalRadioButton.isChecked && rankRadioButton.isChecked -> viewModel.averages.getPromedioGDP(from = fechaInit!!, to = fechaEnd!!).toMaybe()
            averageType == 1 && totalRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.getPromedioGDP(mes = mes, anio = anio).toMaybe()

            averageType == 2 && individualRadioButton.isChecked && rankRadioButton.isChecked -> viewModel.averages.promedioDiasVaciosTotalYBovino(bovino = bovino!!._id!!, from = fechaInit!!, to = fechaEnd!!)
            averageType == 2 && individualRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.promedioDiasVaciosTotalYBovino(bovino = bovino!!._id!!, mes = mes, anio = anio)
            averageType == 2 && totalRadioButton.isChecked && rankRadioButton.isChecked -> viewModel.averages.getPromedioDiasVacios(from = fechaInit!!, to = fechaEnd!!)
            averageType == 2 && totalRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.getPromedioDiasVacios(mes = mes, anio = anio)

            averageType == 3 && individualRadioButton.isChecked -> viewModel.averages.promedioIntervaloPartosTotalYBovino(bovino = bovino!!._id!!, from = fechaInit!!, to = fechaEnd!!)
            // averageType == 3 && individualRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.promedioIntervaloPartosTotalYBovino(bovino = bovino!!._id!!, mes = mes, anio = anio)
            averageType == 3 && totalRadioButton.isChecked -> viewModel.averages.getPromedioIntervaloPartos(from = fechaInit!!, to = fechaEnd!!)
            // averageType == 3 && totalRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.getPromedioIntervaloPartos(mes = mes, anio = anio)

            averageType == 4 -> viewModel.averages.getPromedioEdad(fechaS!!)

            averageType == 5 && totalRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.getPromedioAlimentacionPorTipo(alimento, mes = mes, anio = anio)
            averageType == 5 && totalRadioButton.isChecked && rankRadioButton.isChecked -> viewModel.averages.getPromedioAlimentacionPorTipo(alimento, from = fechaInit!!, to = fechaEnd!!)
            averageType == 5 && individualRadioButton.isChecked && monthlyRadioButton.isChecked -> viewModel.averages.getPromedioAlimentacionPorTipo(alimento, mes = mes, anio = anio, bovino = bovino!!._id!!)
            averageType == 5 && individualRadioButton.isChecked && rankRadioButton.isChecked -> viewModel.averages.getPromedioAlimentacionPorTipo(alimento, from = fechaInit!!, to = fechaEnd!!, bovino = bovino!!._id!!)


            averageType == 6 && monthlyRadioButton.isChecked -> viewModel.averages.totalAbortos(month = mes, year=anio).toMaybe()
            averageType == 6 && rankRadioButton.isChecked -> viewModel.averages.totalAbortos(from=fechaInit!!, to=fechaEnd!!).toMaybe()

            averageType == 7 && monthlyRadioButton.isChecked -> viewModel.averages.totalPartos(month = mes, year = anio).toMaybe()
            averageType == 7 && rankRadioButton.isChecked -> viewModel.averages.totalPartos(from=fechaInit!!, to=fechaEnd!!).toMaybe()

            averageType == 8 && monthlyRadioButton.isChecked -> viewModel.averages.totalServicios(month=mes, year=anio).toMaybe()
            averageType == 8 && rankRadioButton.isChecked -> viewModel.averages.totalServicios(from=fechaInit!!, to=fechaEnd!!).toMaybe()

            averageType == 9 && monthlyRadioButton.isChecked -> viewModel.averages.totalServiciosEfectivos(month=mes, year=anio).toMaybe()
            else -> viewModel.averages.totalServiciosEfectivos(from=fechaInit!!, to=fechaEnd!!).toMaybe()


        }
    }

    private fun onAverageTypeChanged(averageType: Int) {
        tipo = averageType
        when (averageType) {
            in 0..1 -> {
                sexSubject.onNext(averageType)
                groupTotOrInd.visibility = View.VISIBLE
                groupBov.visibility = if (individualRadioButton.isChecked) View.VISIBLE else View.GONE
                groupDate.visibility = View.GONE
                groupMonthOrRank.visibility = View.VISIBLE
                groupFeed.visibility = View.GONE
                if (monthlyRadioButton.isChecked) {
                    groupDates.visibility = View.GONE
                    groupMonth.visibility = View.VISIBLE
                } else {
                    groupDates.visibility = View.VISIBLE
                    groupMonth.visibility = View.GONE
                }
            }
            2 -> {
                sexSubject.onNext(0)
                groupTotOrInd.visibility = View.VISIBLE
                groupBov.visibility = if (individualRadioButton.isChecked) View.VISIBLE else View.GONE
                groupMonthOrRank.visibility = View.VISIBLE
                if (monthlyRadioButton.isChecked) {
                    groupDates.visibility = View.GONE
                    groupMonth.visibility = View.VISIBLE
                } else {
                    groupDates.visibility = View.VISIBLE
                    groupMonth.visibility = View.GONE
                }
            }
            3 -> {
                sexSubject.onNext(0)
                groupTotOrInd.visibility = View.VISIBLE
                groupBov.visibility = if (individualRadioButton.isChecked) View.VISIBLE else View.GONE
                groupMonthOrRank.visibility = View.GONE
                groupDates.visibility = View.VISIBLE
                groupMonth.visibility = View.GONE

            }
            4 -> {
                groupTotOrInd.visibility = View.GONE
                groupBov.visibility = View.GONE
                groupDates.visibility = View.GONE
                groupMonthOrRank.visibility = View.GONE
                groupMonth.visibility = View.GONE
                groupDate.visibility = View.VISIBLE
                groupFeed.visibility = View.GONE
            }
            5 -> {
                sexSubject.onNext(1)
                groupTotOrInd.visibility = View.VISIBLE
                groupBov.visibility = View.GONE
                groupDate.visibility = View.GONE
                groupMonthOrRank.visibility = View.VISIBLE
                groupFeed.visibility = View.VISIBLE
                if (monthlyRadioButton.isChecked) {
                    groupDates.visibility = View.GONE
                    groupMonth.visibility = View.VISIBLE
                } else {
                    groupDates.visibility = View.VISIBLE
                    groupMonth.visibility = View.GONE
                }
            }
            else -> {
                groupTotOrInd.visibility = View.GONE
                groupBov.visibility = View.GONE
                groupDate.visibility = View.GONE
                groupMonthOrRank.visibility = View.VISIBLE
                groupFeed.visibility = View.GONE
                if (monthlyRadioButton.isChecked) {
                    groupDates.visibility = View.GONE
                    groupMonth.visibility = View.VISIBLE
                } else {
                    groupDates.visibility = View.VISIBLE
                    groupMonth.visibility = View.GONE
                }
            }
        }
    }

    override fun onDateSet(view: com.borax12.materialdaterangepicker.date.DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        val sDate = "$dayOfMonth/${monthOfYear + 1}/$year - $dayOfMonthEnd/${monthOfYearEnd + 1}/$yearEnd"
        fromDateText.setText(sDate)
        fechaInit = "$dayOfMonth/${monthOfYear + 1}/$year".toDate()
        fechaEnd = "$dayOfMonthEnd/${monthOfYearEnd + 1}/$yearEnd".toDate()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val sDate = "$dayOfMonth/${month + 1}/$year"
        date.setText(sDate)
        fechaS = sDate.toDate()
    }

    companion object {
        fun instance() = SelectAverageFragment()
    }


}
