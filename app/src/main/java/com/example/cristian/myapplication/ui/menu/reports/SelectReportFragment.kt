package com.example.cristian.myapplication.ui.menu.reports


import android.app.DatePickerDialog
import android.app.ListActivity
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
import android.widget.DatePicker
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.data.models.getHeader
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.excel.TemplateExcel
import com.example.cristian.myapplication.pdf.TemplatePdf
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.add
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text
import kotlinx.android.synthetic.main.activity_add_vaccine.*
import kotlinx.android.synthetic.main.fragment_aforo.*
import kotlinx.android.synthetic.main.fragment_select_group.*
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.toast
import java.util.*
import javax.inject.Inject
import com.example.cristian.myapplication.data.models.DatePickerFragment
import kotlin.collections.ArrayList


class SelectReportFragment : Fragment() , Injectable {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    lateinit var binding: FragmentSelectReportBinding
    lateinit var templatePdf:TemplatePdf
    lateinit var templateExcel: TemplateExcel
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewmodel.getFarmId() }
    val options = listOf("PDF","Excel")
    var tiposdata : Array<String> = emptyArray()
    val calendar: Calendar by lazy { Calendar.getInstance() }
    var selectedType : String = "Partos"

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

        var header : List<String> = emptyList()


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

        dis add reportType.itemSelections()
                .subscribeBy(onNext = {
                    selectedType = tiposdata[it]


                })

        dis add fabView.clicks()
                .subscribeBy (
                        onNext = {
                          header = getHeader(selectedType)
                            dis add  viewmodel.getHealth(idFinca).subscribeBy(
                                    onSuccess = {
                                        selector("Seleccione el formato que desea", options) { dialogInterface, i ->
                                            if (i==0)
                                                else templateExcel.saveExcelFile("reporte.xlsx",1,activity!!)}
                                    }
                            )

                        }
                )
      /*  dis add fabDownload.clicks()
                .subscribeBy {

                    selector("Seleccione el formato que desea", options) { dialogInterface, i ->
                        if (i==0)  pdf("reporte",2,header,)
                        else  templateExcel.saveExcelFile("reporte.xlsx",2,activity!!)
                        toast("Reporte guardado")

                    }
                }
*/
    }




    private  fun pdf(nombre:String,dir:Int,header:List<String>,list:List<List<String>>){
        var array :List<String> = listOf("123","131312","sd","sd","sd","dwe","123","131312","sd","sd","sd","dwe","123","131312","sd","sd","sd","dwe")
        /*for (item in list){
            array=array.plus(listOf(item.producto!!,item.tratamiento!!,item.evento!!,item.diagnostico!!))
        }*/

        templatePdf.openFile("reporte", dir)
        templatePdf.addTitle("Hola","Mundo",Calendar.getInstance().time.toString())
        templatePdf.createTable(header, list)
        templatePdf.closeFile()
        if (dir==1) templatePdf.ViewPdf()
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

    private fun setEntries(tipos: Array<String>){
        val spinnerArrayAdapter = ArrayAdapter<String>(this.context, R.layout.support_simple_spinner_dropdown_item, tipos)
        reportType.adapter = spinnerArrayAdapter
            tiposdata = tipos

    }

//region obtenerDatos
    private fun obtenerReportes(header: List<String>, month:Int, from:Date, to:Date):List<List<String>>{
    var listareportes:List<List<String>> = emptyList()
            when(selectedType){
                "Partos"->if (monthlyRadioButton.isChecked) viewmodel.reportePreparacion(month,Calendar.YEAR).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}

                )
                          else viewmodel.reportePreparacion(from,to).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}

                )
                "Secado"->if (monthlyRadioButton.isChecked) viewmodel.reporteSecado(month,Calendar.YEAR).subscribeBy(
                        onSuccess = {  pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                          else  viewmodel.reporteSecado(from,to).subscribeBy(
                        onSuccess = {  pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                "Preparación"->if (monthlyRadioButton.isChecked) viewmodel.reportePreparacion(month,Calendar.YEAR).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                        else viewmodel.reportePreparacion(from, to).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )

                "Días abiertos"-> viewmodel.reporteDiasVacios().subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )

                "Partos atendidos"->if (monthlyRadioButton.isChecked) viewmodel.reportePartosAtendidos(month,Calendar.YEAR).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                                    else viewmodel.reportePartosAtendidos(from,to).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                "Abortos"-> if (monthlyRadioButton.isChecked) viewmodel.reporteAbortos(month,Calendar.YEAR).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                            else viewmodel.reporteAbortos(from, to).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                "Tres servicios"-> viewmodel.reporteTresServicios().subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                "Celos"-> viewmodel.reporteCelos().subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                //"Resumen reproductivo"->

            //LECHE
                //"Consolidado de leche"-> listOf(listOf())
                "Reporte de leche"->{
                    var leche :List<List<String>> = emptyList()
                    if (monthlyRadioButton.isChecked) viewmodel.getBovine(idFinca).subscribeBy(
                        onSuccess = {
                            for (bovino in it){
                                viewmodel.reportesLeche(bovino,month,Calendar.YEAR).subscribeBy(
                                        onSuccess = {
                                            leche = leche.plus(it) })}
                            pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,leche)} )

                            else viewmodel.getBovine(idFinca).subscribeBy {
                        for (bovino in it){
                            viewmodel.reportesLeche(bovino,from, to).subscribeBy(
                                    onSuccess = {
                                        leche = leche.plus(it)
                                     })     }

                            pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,leche)
                    }
                }
            //CEBA
                "Destetos"-> if (monthlyRadioButton.isChecked) viewmodel.reportesDestete(month,Calendar.YEAR).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)} )
                            else viewmodel.reportesDestete(from,to).subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                "Ganancia diaria de peso"-> {
                    var gdp:List<List<String>> = emptyList()
                    if (monthlyRadioButton.isChecked) viewmodel.getBovine(idFinca).subscribeBy(
                            onSuccess = {
                                for (bovino in it){
                                    viewmodel.reporteGananciaPeso(bovino,month,Calendar.YEAR).subscribeBy(
                                            onSuccess = {
                                                gdp = gdp.plus(it)
                                                listareportes = gdp
                                                pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,listareportes)
                                            })     }} )
                    else viewmodel.getBovine(idFinca).subscribeBy {
                        for (bovino in it){
                            viewmodel.reporteGananciaPeso(bovino,from, to).subscribeBy(
                                    onSuccess = {
                                        gdp = gdp.plus(it)
                                        listareportes = gdp
                                        pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,listareportes)
                                    })     }}
                }
            //PRADERAS
                //"Praderas"->
                //"Ocupación de praderas"-> listOf(listOf())
            //ALIMENTACION
                "Alimentacion"-> {
                    var alimentacion:List<List<String>> = emptyList()
                    if (monthlyRadioButton.isChecked) viewmodel.getBovine(idFinca).subscribeBy(
                            onSuccess = {
                                for (bovino in it){
                                    viewmodel.reporteAlimentacion(bovino,month,Calendar.YEAR).subscribeBy(
                                            onSuccess = {
                                                alimentacion = alimentacion.plus(it)})}
                                pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,alimentacion)} )

                    else viewmodel.getBovine(idFinca).subscribeBy {
                        for (bovino in it){
                            viewmodel.reporteGananciaPeso(bovino,from, to).subscribeBy(
                                    onSuccess = {
                                        alimentacion = alimentacion.plus(it)
                                        pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,alimentacion)
                                    })     }}
                }
                //"Suplementos usados"-> listOf(listOf())
            //MOVIMIENTOS
                //"Animales en pradera"-> listOf(listOf())
            //ENTRADAS
                "Inventario"->  if (monthlyRadioButton.isChecked) viewmodel.reporteInventario(month,Calendar.YEAR).subscribeBy(
                        onSuccess = {  pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                                else viewmodel.reporteInventario(from, to).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                "Terneras en estaca"-> if (monthlyRadioButton.isChecked) viewmodel.reporteTernerasEnEstaca(month,Calendar.YEAR).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                else viewmodel.reporteTernerasEnEstaca(from, to).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                "Terneras destetas"-> if (monthlyRadioButton.isChecked) viewmodel.reporteTernerasDestetas(month,Calendar.YEAR).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                else viewmodel.reporteTernerasDestetas(from, to).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                "Novillas de levante"-> if (monthlyRadioButton.isChecked) viewmodel.reporteTerneraslevante(month,Calendar.YEAR).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                else viewmodel.reporteTerneraslevante(from, to).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                "Novillas vientre"-> if (monthlyRadioButton.isChecked) viewmodel.reporteNovillasVientre(month,Calendar.YEAR).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                else viewmodel.reporteNovillasVientre(from, to).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
                //"Horras"-> listOf("Codigo","Nombre","Nacimiento","Proposito","Raza")
                "Vacas"-> viewmodel.reporteVacas().subscribeBy(
                        onSuccess = { pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )

            //SALIDAS
               // "Salida"-> listOf("Codigo","Nombre","Fecha salida","Tipo salida")

            //VACUNAS
                "Vacunas"-> if (monthlyRadioButton.isChecked) viewmodel.reporteVacunas(month,Calendar.YEAR).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )           else viewmodel.reporteVacunas(from, to).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
            //SANIDAD
                "Sanidad"-> viewmodel.reportesPrueba().subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it) }
                )

            //MANEJO
                "Manejo"->  if (monthlyRadioButton.isChecked) viewmodel.reporteManejo(month,Calendar.YEAR).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
            )               else viewmodel.reporteManejo(from, to).subscribeBy(
                        onSuccess = {pdf("reporte "+categoriesSpinner.selectedItem+" "+Calendar.MONTH ,1,header,it)}
                )
            //PAJILLAS
             //   "Pajillas"-> if (monthlyRadioButton.isChecked) viewmodel.re
                else -> emptyList<String>() }
                return  listareportes}


// endregion


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
