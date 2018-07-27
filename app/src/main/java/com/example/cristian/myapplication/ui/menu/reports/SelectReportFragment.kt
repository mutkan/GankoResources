package com.example.cristian.myapplication.ui.menu.reports


import android.content.Context
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
import android.widget.ArrayAdapter
import com.example.cristian.myapplication.excel.TemplateExcel
import com.example.cristian.myapplication.pdf.TemplatePdf
import com.example.cristian.myapplication.ui.menu.MenuActivity
import com.jakewharton.rxbinding2.view.clicks
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintStream


class SelectReportFragment : Fragment() {

    lateinit var binding: FragmentSelectReportBinding
    lateinit var templatePdf:TemplatePdf
    lateinit var templateExcel: TemplateExcel
    val dis: LifeDisposable = LifeDisposable(this)
    val header = arrayOf("id","nombre","edad")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_report, container, false)
        templatePdf = TemplatePdf(context!!)
        templateExcel = TemplateExcel(context!!)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val options = listOf("PDF","Excel")


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

        dis add fabView.clicks()
                .subscribeBy (
                        onNext = {

                            selector("Seleccione el formato que desea", options) { dialogInterface, i ->
                                if (i==0)  pdf("reporte",1)
                                else templateExcel.saveExcelFile("reporte.xlsx",1,activity!!)
                                    //templateExcel.viewExcel(activity!!)


                            }
                        }
                )
        dis add fabDownload.clicks()
                .subscribeBy {

                    selector("Seleccione el formato que desea", options) { dialogInterface, i ->
                        if (i==0)  pdf("reporte",2)
                        else  templateExcel.saveExcelFile("reporte.xlsx",2,activity!!)
                        toast("Reporte guardado")

                    }
                }

    }




    private  fun pdf(nombre:String,dir:Int){
        templatePdf.openFile("reporte", dir)
        templatePdf.addData("Reportes sanidad","")
        templatePdf.addTitle("Hola","Mundo","12/0/2018")
        templatePdf.createTable(header,datos())
        templatePdf.closeFile()
        if (dir==1) templatePdf.ViewPdf()
    }



    private fun datos():ArrayList<List<String>> = arrayListOf(listOf("1","Dda","21"),listOf("2","pp","32"), listOf("33","sd","sd"))


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
