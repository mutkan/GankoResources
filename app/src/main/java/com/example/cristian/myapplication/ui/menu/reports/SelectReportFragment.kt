package com.example.cristian.myapplication.ui.menu.reports


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
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


class SelectReportFragment : Fragment() {

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

        dis add typeDateGroup.checkedChanges()
                .subscribeBy(
                        onNext = {
                            binding.monthly = monthlyRadioButton.isChecked
                        }
                )

        dis add categoriesSpinner.itemSelections()
                .subscribeBy(
                        onNext = {
                            //when (it){
                            //REPORTE_REPRODUCTIVOS -> binding.reportType
                            //}
                        }
                )
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
