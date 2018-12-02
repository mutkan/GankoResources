package com.ceotic.ganko.ui.menu.reports


import android.Manifest
import android.arch.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.getHeader
import com.ceotic.ganko.databinding.FragmentSelectReportBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.excel.TemplateExcel
import com.ceotic.ganko.pdf.TemplatePdf
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.toDate
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.itemSelections
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_select_report.*
import org.jetbrains.anko.support.v4.selector
import java.util.*
import javax.inject.Inject


class SelectReportFragment : Fragment(), Injectable, com.borax12.materialdaterangepicker.date.DatePickerDialog.OnDateSetListener {


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    lateinit var binding: FragmentSelectReportBinding
    lateinit var templatePdf: TemplatePdf
    lateinit var templateExcel: TemplateExcel
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewmodel.getFarmId() }
    val options = listOf("PDF", "Excel")
    var tiposdata: Array<String> = emptyArray()
    var fechaInit: Date = Date()
    var fechaEnd: Date = Date()
    val calendar: Calendar by lazy { Calendar.getInstance() }
    val year: Int by lazy { calendar.get(Calendar.YEAR) }
    var datePicker: com.borax12.materialdaterangepicker.date.DatePickerDialog = com.borax12.materialdaterangepicker.date.DatePickerDialog
            .newInstance(this, year, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    var selectedType: String = "Partos"


    override fun onDateSet(view: com.borax12.materialdaterangepicker.date.DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        val sDate = "$dayOfMonth/${monthOfYear + 1}/$year - $dayOfMonthEnd/${monthOfYearEnd + 1}/$yearEnd"
        fromDateText.text = sDate
        fechaInit = "$dayOfMonth/${monthOfYear + 1}/$year".toDate()
        fechaEnd = "$dayOfMonthEnd/${monthOfYearEnd + 1}/$yearEnd".toDate()
    }

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
        var MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 0
        val permissionCheck1 = ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE)


        if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(activity!!,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_CONTACTS)

            }
        }

        var header: List<String> = emptyList()
        dis add fromDateText.clicks()
                .subscribeBy {
                    datePicker.show(activity!!.fragmentManager, "dialog")
                }

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
                .subscribeBy(
                        onNext = {
                            header = getHeader(selectedType)

                            selector("Seleccione el formato que desea", options) { dialogInterface, i ->
                                if (i == 0) obtenerReportesPdf(header, spinnerMonth.selectedItemPosition, fechaInit, fechaEnd, 1)
                                else obtenerReportesExcel(header, spinnerMonth.selectedItemPosition, fechaInit, fechaEnd, 1)
                            }
                        })
        dis add fabDownload.clicks()
                .subscribeBy(
                        onNext = {
                            header = getHeader(selectedType)
                            selector("Seleccione el formato que desea", options) { dialogInterface, i ->
                                if (i == 0) obtenerReportesPdf(header, spinnerMonth.selectedItemPosition, fechaInit, fechaEnd, 2)
                                else obtenerReportesExcel(header, spinnerMonth.selectedItemPosition, fechaInit, fechaEnd, 2)
                            }
                        })
    }


    private fun pdf(nombre: String, dir: Int, header: List<String>, list: List<List<String>>) {
        templatePdf.openFile(nombre, dir)
        templatePdf.addTitle("Reporte $selectedType ", Calendar.getInstance().time.toString())
        templatePdf.createTable(header, list)
        templatePdf.closeFile()
        if (dir == 1) {
            templatePdf.viewPdf(activity!!)
        } else {
            Snackbar.make(view!!, "Reporte guardado en descargas", Snackbar.LENGTH_SHORT).setAction("VER") {
                templatePdf.viewPdf(activity!!)
            }.show()
        }
    }

    private fun excel(nombre: String, dir: Int, header: List<String>, list: List<List<String>>) {
        templateExcel.saveExcelFile(nombre, dir, activity!!, header, list, view!!)
    }

    private fun checkCategoriesSpinnerChanges(selected: Int) {
        when (selected) {
            REPORTE_REPRODUCTIVOS -> setEntries(resources.getStringArray(R.array.reproductive_type))
            REPORTE_PROD_LECHE -> setEntries(resources.getStringArray(R.array.milk_type))
            REPORTE_CEBA -> setEntries(resources.getStringArray(R.array.meat_type))
            REPORTE_PRADERAS -> setEntries(resources.getStringArray(R.array.meadows_type))
            REPORTE_ALIMENTACION -> setEntries(resources.getStringArray(R.array.feed_type))
            REPORTE_MOVIMIENTOS -> setEntries(resources.getStringArray(R.array.movements_type))
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
        tiposdata = tipos

    }

    //region obtenerDatos
    private fun obtenerReportesPdf(header: List<String>, month: Int, from: Date, to: Date, dir: Int) {
        var listareportes: List<List<String>> = emptyList()
        when (selectedType) {
            "Partos futuros" -> if (monthlyRadioButton.isChecked) viewmodel.reporteFuturosPartos(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }

            )
            else viewmodel.reporteFuturosPartos(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }

            )
            "Secado" -> if (monthlyRadioButton.isChecked) viewmodel.reporteSecado(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteSecado(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Preparación" -> if (monthlyRadioButton.isChecked) viewmodel.reportePreparacion(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reportePreparacion(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            "Días abiertos" -> viewmodel.reporteDiasVacios().subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            "Partos atendidos" -> if (monthlyRadioButton.isChecked) viewmodel.reportePartosAtendidos(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reportePartosAtendidos(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Abortos" -> if (monthlyRadioButton.isChecked) viewmodel.reporteAbortos(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteAbortos(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Tres servicios" -> viewmodel.reporteTresServicios().subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Celos" -> viewmodel.reporteCelos(month,year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            //LECHE
            "Consolidado de leche"-> {

                if (monthlyRadioButton.isChecked) viewmodel.reporteConsolidado(month, year).subscribeBy(
                        onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
                )
                else viewmodel.reporteConsolidado(from,to).subscribeBy(
                        onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
                )
            }

            "Reporte de leche" -> if (monthlyRadioButton.isChecked) viewmodel.reportesLeche(month,year).subscribeBy(
                    onSuccess = {pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it)})
                else viewmodel.reportesLeche(from,to).subscribeBy(
                    onSuccess = {pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it)}
                )

            //CEBA
            "Destetos" -> if (monthlyRadioButton.isChecked) viewmodel.reportesDestete(from,to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) })
            else viewmodel.reportesDestete(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Ganancia diaria de peso" -> {
                var gdp: List<List<String>> = emptyList()
                if (monthlyRadioButton.isChecked) viewmodel.getBovine(idFinca).subscribeBy(
                        onSuccess = {
                            for (bovino in it) {
                                viewmodel.reporteGananciaPeso(bovino, month, year).subscribeBy(
                                        onSuccess = {
                                            gdp = gdp.plus(it)
                                            listareportes = gdp
                                            pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, listareportes)
                                        })
                            }
                        })
                else viewmodel.getBovine(idFinca).subscribeBy {
                    for (bovino in it) {
                        viewmodel.reporteGananciaPeso(bovino, from, to).subscribeBy(
                                onSuccess = {
                                    gdp = gdp.plus(it)
                                    listareportes = gdp
                                    pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, listareportes)
                                })
                    }
                }
            }
            //PRADERAS
            "Praderas" -> viewmodel.reporteGetPraderas().subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
           /* "Ocupación de praderas" -> viewmodel.reporteOcupacionPraderas().subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )*/
            //ALIMENTACION
            "Alimentación" -> {
                var alimentacion: List<List<String>> = emptyList()
                if (monthlyRadioButton.isChecked) viewmodel.reporteAlimentacion( month, year).subscribeBy(
                        onSuccess = {
                            pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it)

                        })



                else viewmodel.reporteAlimentacion( from, to).subscribeBy(
                        onSuccess = {
                            pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it)

                        })
            }

            //ENTRADAS
            "Inventario" -> if (monthlyRadioButton.isChecked) viewmodel.reporteInventario(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteInventario(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Terneras en estaca" -> if (monthlyRadioButton.isChecked) viewmodel.reporteTernerasEnEstaca(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteTernerasEnEstaca(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Terneras destetas" -> if (monthlyRadioButton.isChecked) viewmodel.reporteTernerasDestetas(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteTernerasDestetas(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Novillas de levante" -> if (monthlyRadioButton.isChecked) viewmodel.reporteTerneraslevante(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteTerneraslevante(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Novillas vientre" -> if (monthlyRadioButton.isChecked) viewmodel.reporteNovillasVientre(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteNovillasVientre(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            "Vacas" -> viewmodel.reporteVacas().subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            //SALIDAS
            // "Salida"-> listOf("Codigo","Nombre","Fecha salida","Tipo salida")

            //VACUNAS
            "Vacunas" -> if (monthlyRadioButton.isChecked) viewmodel.reporteVacunas(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            ) else viewmodel.reporteVacunas(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            //SANIDAD
            "Sanidad" -> if (monthlyRadioButton.isChecked) viewmodel.reporteSanidad(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            ) else viewmodel.reporteSanidad(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            //MANEJO
            "Manejo" -> if (monthlyRadioButton.isChecked) viewmodel.reporteManejo(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            ) else viewmodel.reporteManejo(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            //PAJILLAS

            "Pajillas" -> if (monthlyRadioButton.isChecked) viewmodel.reportePajillas(month, year).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            ) else viewmodel.reportePajillas(from, to).subscribeBy(
                    onSuccess = { pdf("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            //   "Pajillas"-> if (monthlyRadioButton.isChecked) viewmodel.re
            else -> emptyList<String>()
        }
    }


    private fun obtenerReportesExcel(header: List<String>, month: Int, from: Date, to: Date, dir: Int) {
        var listareportes: List<List<String>> = emptyList()
        when (selectedType) {
            "Partos futuros" -> if (monthlyRadioButton.isChecked) viewmodel.reporteFuturosPartos(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }

            )
            else viewmodel.reporteFuturosPartos(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }

            )
            "Secado" -> if (monthlyRadioButton.isChecked) viewmodel.reporteSecado(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteSecado(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Preparación" -> if (monthlyRadioButton.isChecked) viewmodel.reportePreparacion(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reportePreparacion(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            "Días abiertos" -> viewmodel.reporteDiasVacios().subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            "Partos atendidos" -> if (monthlyRadioButton.isChecked) viewmodel.reportePartosAtendidos(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reportePartosAtendidos(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Abortos" -> if (monthlyRadioButton.isChecked) viewmodel.reporteAbortos(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteAbortos(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Tres servicios" -> viewmodel.reporteTresServicios().subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Celos" -> viewmodel.reporteCelos(month,year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            //LECHE
            "Consolidado de leche"-> {
                if (monthlyRadioButton.isChecked) viewmodel.reporteConsolidado(month, year).subscribeBy(
                        onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
                )
                else viewmodel.reporteConsolidado(from,to).subscribeBy(
                        onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
                )
            }

            "Reporte de leche" -> {
                var leche: List<List<String>> = emptyList()
                if (monthlyRadioButton.isChecked) viewmodel.reportesLeche(month,year).subscribeBy(
                        onSuccess = {
                            excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it)})
                else viewmodel.reportesLeche(from,to).subscribeBy(
                        onSuccess = {excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it)}
                )

                        }


            //CEBA
            "Destetos" -> if (monthlyRadioButton.isChecked) viewmodel.reportesDestete(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) })
            else viewmodel.reportesDestete(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Ganancia diaria de peso" -> {
                var gdp: List<List<String>> = emptyList()
                if (monthlyRadioButton.isChecked) viewmodel.getBovine(idFinca).subscribeBy(
                        onSuccess = {
                            for (bovino in it) {
                                viewmodel.reporteGananciaPeso(bovino, month, year).subscribeBy(
                                        onSuccess = {
                                            gdp = gdp.plus(it)
                                            listareportes = gdp
                                        })
                            }
                            excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, listareportes)

                        }
                )
                else viewmodel.getBovine(idFinca).subscribeBy(
                        onSuccess = {
                            for (bovino in it) {
                                viewmodel.reporteGananciaPeso(bovino, from, to).subscribeBy(
                                        onSuccess = {
                                            gdp = gdp.plus(it)
                                            listareportes = gdp
                                        })
                            }
                            excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, listareportes)
                        }
                )
            }
            //PRADERAS
            "Praderas" -> viewmodel.reporteGetPraderas().subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
           /* "Ocupación de praderas" -> viewmodel.reporteOcupacionPraderas().subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )*/
            //ALIMENTACION
            "Alimentación" -> {
                var alimentacion: List<List<String>> = emptyList()
                if (monthlyRadioButton.isChecked) viewmodel.reporteAlimentacion( month, year).subscribeBy(
                                onSuccess = {
                                    excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it)

                                })



                else viewmodel.reporteAlimentacion( from, to).subscribeBy(
                        onSuccess = {
                            excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it)

                        })
            }

            //ENTRADAS
            "Inventario" -> if (monthlyRadioButton.isChecked) viewmodel.reporteInventario(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteInventario(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Terneras en estaca" -> if (monthlyRadioButton.isChecked) viewmodel.reporteTernerasEnEstaca(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteTernerasEnEstaca(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Terneras destetas" -> if (monthlyRadioButton.isChecked) viewmodel.reporteTernerasDestetas(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteTernerasDestetas(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Novillas de levante" -> if (monthlyRadioButton.isChecked) viewmodel.reporteTerneraslevante(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteTerneraslevante(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            "Novillas vientre" -> if (monthlyRadioButton.isChecked) viewmodel.reporteNovillasVientre(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            else viewmodel.reporteNovillasVientre(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            "Vacas" -> viewmodel.reporteVacas().subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            //SALIDAS
            // "Salida"-> listOf("Codigo","Nombre","Fecha salida","Tipo salida")

            //VACUNAS
            "Vacunas" -> if (monthlyRadioButton.isChecked) viewmodel.reporteVacunas(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            ) else viewmodel.reporteVacunas(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            //SANIDAD
            "Sanidad" -> if (monthlyRadioButton.isChecked) viewmodel.reporteSanidad(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            ) else viewmodel.reporteSanidad(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )

            //MANEJO
            "Manejo" -> if (monthlyRadioButton.isChecked) viewmodel.reporteManejo(month, year).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            ) else viewmodel.reporteManejo(from, to).subscribeBy(
                    onSuccess = { excel("reporte " + categoriesSpinner.selectedItem + " " + Calendar.MONTH, dir, header, it) }
            )
            //PAJILLAS
            //   "Pajillas"-> if (monthlyRadioButton.isChecked) viewmodel.re
            else -> emptyList<String>()
        }
    }


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
