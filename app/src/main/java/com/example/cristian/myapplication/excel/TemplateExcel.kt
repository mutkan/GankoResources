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

    fun saveExcelFile( fileName: String, opcion:Int,activity: Activity,header:List<String>,lista:List<List<String>>) {
        val wb = HSSFWorkbook()
        var c: Cell? = null
        var sheet1: Sheet? = null
        sheet1 = wb.createSheet("GankoReports")

        // Generate column headings
        val row = sheet1!!.createRow(0)
        for ((index,item) in header.withIndex()){
            c = row.createCell(index)
            c!!.setCellValue(item)
            sheet1.setColumnWidth(index, 15 * 500) }


        for ((index,item) in lista.withIndex()){
        val fila = sheet1!!.createRow(index+1)
            for ((index1,item1) in item.withIndex()){
                c = fila.createCell(index1)
                c!!.setCellValue(item1)
                sheet1.setColumnWidth(index, 15 * 500)
            }
        }

        excelfile= if (opcion==2){
            val folder : File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
            if (!folder.exists()) folder.mkdir()
              File(folder,fileName+Date()+".xls")
        }
        else
        {
            File.createTempFile(fileName+Date()+".xls", null, context.cacheDir)
        }


        var os = FileOutputStream(excelfile)
            wb.write(os)
        os.close()
        if (opcion==1) viewExcel(activity)

    }

    fun viewExcel(activity: Activity){
        if (excelfile.exists()) {
            val uri: Uri = Uri.fromFile(excelfile)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/xls")
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=cn.wps.moffice_eng")))

        }else
            activity.toast("No se encontro el documento")
    }

}

