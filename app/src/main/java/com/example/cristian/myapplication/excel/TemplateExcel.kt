package com.example.cristian.myapplication.excel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.os.Environment.MEDIA_MOUNTED
import android.os.Environment.MEDIA_MOUNTED_READ_ONLY
import android.support.v4.content.ContextCompat.startActivity
import android.widget.Toast
import com.example.cristian.myapplication.data.models.Customer
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.hssf.record.aggregates.RowRecordsAggregate.createRow
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.*
import org.jetbrains.anko.toast
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class TemplateExcel(var context: Context){
    lateinit var excelfile :File

    fun saveExcelFile( fileName: String, opcion:Int,activity: Activity) {
        val wb = HSSFWorkbook()
        var c: Cell? = null
        var sheet1: Sheet? = null
        sheet1 = wb.createSheet("myOrder")

        // Generate column headings
        val row = sheet1!!.createRow(0)

        c = row.createCell(0)
        c!!.setCellValue("Item Number")

        c = row.createCell(1)
        c!!.setCellValue("Quantity")

        c = row.createCell(2)
        c!!.setCellValue("Price")

        sheet1!!.setColumnWidth(0, 15 * 500)
        sheet1!!.setColumnWidth(1, 15 * 500)
        sheet1!!.setColumnWidth(2, 15 * 500)
        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
        if (!folder.exists())
            folder.mkdir()

        excelfile = if (opcion==1) File.createTempFile("reporte", null, context.getCacheDir())
        else File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),fileName)


        var os = FileOutputStream(excelfile)
            wb.write(os)
                os?.close()
        if (opcion==1) viewExcel(activity)

    }

    fun viewExcel(activity: Activity){
        val uri:Uri = Uri.fromFile(excelfile)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri,"application/xlsx")
        activity.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=cn.wps.moffice_eng")))
        activity.toast("Usted no cuenta con una aplicacion para abrir el documento")

    }

}

