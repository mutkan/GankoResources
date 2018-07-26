package com.example.cristian.myapplication.ui.menu.reports


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentSelectReportBinding
import com.example.cristian.myapplication.util.LifeDisposable
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.itemSelections
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_select_report.*
import android.widget.ArrayAdapter
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.add
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import org.jetbrains.anko.support.v4.toast
import java.util.*
import javax.inject.Inject


class SelectReportFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }

    lateinit var binding: FragmentSelectReportBinding

    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_report, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dis add fabView.clicks()
                .flatMapSingle {
                    val from = Date()
                    val to: Date by lazy { from.add(Calendar.DATE, 7)!! }
                    viewmodel.reporteManejo(from, to)
                }
                .subscribeBy(
                        onNext = {
                            Log.d("REPORTE", it.toString())
                            toast("FUNCAAA !!"+ it.toString())
                        }
                )

        dis add typeDateGroup.checkedChanges()
                .subscribeBy(
                        onNext = {
                            binding.monthly = monthlyRadioButton.isChecked
                        }
                )

        dis add categoriesSpinner.itemSelections()
                .subscribeBy(
                        onNext = {
                            checkCategoriesSpinnerChanges(it)
                        }
                )
    }

     private fun checkCategoriesSpinnerChanges(selected: Int){
        when (selected) {
            REPORTE_REPRODUCTIVOS -> setEntries(resources.getStringArray(R.array.reproductive_type))
            REPORTE_PROD_LECHE -> setEntries(resources.getStringArray(R.array.milk_type))
            REPORTE_CEBA ->  setEntries(resources.getStringArray(R.array.meat_type))
            REPORTE_PRADERAS -> setEntries(resources.getStringArray(R.array.meadows_type))
            REPORTE_ALIMENTACION -> setEntries(resources.getStringArray(R.array.feed_type))
            REPORTE_MOVIMIENTOS ->  setEntries(resources.getStringArray(R.array.movements_type))
            REPORTE_ENTRADAS -> setEntries(resources.getStringArray(R.array.input_type))
            REPORTE_SALIDAS -> setEntries(resources.getStringArray(R.array.output_type))
            REPORTE_VACUNAS -> setEntries(resources.getStringArray(R.array.vaccine_type))
            REPORTE_SANIDAD -> setEntries(resources.getStringArray(R.array.health_type))
            REPORTE_MANEJO -> setEntries(resources.getStringArray(R.array.manage_type))
            REPORTE_PAJILLAS -> setEntries(resources.getStringArray(R.array.straw__reports_type))
        }
    }

    private fun setEntries(tipos: Array<String>) {
        val spinnerArrayAdapter = ArrayAdapter<String>(this.context, R.layout.support_simple_spinner_dropdown_item, tipos)
        reportType.adapter = spinnerArrayAdapter
    }

    companion object {
        const val REPORTE_REPRODUCTIVOS = 0
        const val REPORTE_PROD_LECHE = 1
        const val REPORTE_CEBA = 2
        const val REPORTE_PRADERAS = 3
        const val REPORTE_ALIMENTACION = 4
        const val REPORTE_MOVIMIENTOS = 5
        const val REPORTE_ENTRADAS = 6
        const val REPORTE_SALIDAS = 7
        const val REPORTE_VACUNAS = 8
        const val REPORTE_SANIDAD = 9
        const val REPORTE_MANEJO = 10
        const val REPORTE_PAJILLAS = 11

        fun instance(): SelectReportFragment = SelectReportFragment()
    }


}
