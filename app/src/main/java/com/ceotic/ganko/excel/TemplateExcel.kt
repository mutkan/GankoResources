package com.ceotic.ganko.excel

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.util.*


class TemplateExcel(var context: Context) {

    fun saveExcelFile(fileName: String, opcion: Int, activity: Activity, header: List<String>, lista: List<List<String>>, view: View) {
        val wb = HSSFWorkbook()
        var c: Cell? = null
        var sheet1: Sheet? = null
        sheet1 = wb.createSheet(fileName)

        // Generate column headings
        val row = sheet1!!.createRow(0)
        for ((index, item) in header.withIndex()) {
            c = row.createCell(index)
            c!!.setCellValue(item)
            sheet1.setColumnWidth(index, 15 * 500)
        }


        for ((index, item) in lista.withIndex()) {
            val fila = sheet1!!.createRow(index + 1)
            for ((index1, item1) in item.withIndex()) {
                c = fila.createCell(index1)
                c!!.setCellValue(item1)
                sheet1.setColumnWidth(index, 15 * 500)
            }
        }

        val excelfile = if (opcion == 2) {
            val folder: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
            if (!folder.exists()) folder.mkdir()
            File(folder, fileName + Date() + ".xls")
        } else {
            File.createTempFile(fileName + Date(), ".xls", context.filesDir)
        }


        var os = FileOutputStream(excelfile)
        wb.write(os)
        os.close()
        if (opcion == 1) {
            viewExcel(activity, excelfile)
        } else {
            activity.run {
                Snackbar.make(view, "Reporte guardado en descargas", Snackbar.LENGTH_SHORT).setAction("VER") {
                    viewExcel(this, excelfile)
                }.show()
            }
        }

    }

    fun viewExcel(activity: Activity, excelfile: File) {
        if (excelfile.exists()) {
            val uri: Uri = FileProvider.getUriForFile(activity.applicationContext, activity.applicationContext.packageName + ".provider", excelfile)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                activity.startActivity(intent)
            } catch (e:ActivityNotFoundException) {
                Log.e("ERROR ABRIENDO XLS", e.message, e)
                activity.toast("No hay una aplicacion para abrir el archivo")
            }

        } else
            activity.toast("No se encontro el documento")
    }

}

