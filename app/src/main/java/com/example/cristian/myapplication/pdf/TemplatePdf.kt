package com.example.cristian.myapplication.pdf

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream


class TemplatePdf(var context: Context) {

    lateinit var pdfFile: File
    lateinit var document:Document
    lateinit var pdfWriter: PdfWriter
    lateinit var paragraph: Paragraph
    lateinit var downloadManager: DownloadManager
    private val fontFile:Font = Font(Font.FontFamily.TIMES_ROMAN,20f,Font.BOLD, BaseColor.RED)
    private val fontSubtitle:Font = Font(Font.FontFamily.TIMES_ROMAN,18f,Font.BOLD)
    private val fontText:Font = Font(Font.FontFamily.TIMES_ROMAN,12f,Font.BOLD)



    fun openFile(reportName: String,dir:Int){
        createFile(reportName,dir)
        document = Document(PageSize.A4)
        pdfWriter = PdfWriter.getInstance(document,FileOutputStream(pdfFile))
        document.open()
    }
    fun  createFile(reportName:String,dir: Int){
        if (dir==2){
        val folder : File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
        if (!folder.exists())
            folder.mkdir()

        pdfFile = File(folder,reportName+".pdf") }
        else
        {   pdfFile= File.createTempFile("reporte", null, context.getCacheDir()) }
        }

    fun closeFile()= document.close()

    fun addData(title:String,subject:String){
        document.addTitle(title)
        document.addSubject(subject)
    }

    fun  addTitle(title: String,subject: String,date:String){
        paragraph = Paragraph()
        addChild(Paragraph(title,fontFile))
        addChild(Paragraph(subject,fontSubtitle))
        addChild(Paragraph("Generado"+date,fontText))
        paragraph.spacingAfter=30f
        document.add(paragraph)


    }

    private fun addChild(childParagraph: Paragraph){
        childParagraph.alignment = Element.ALIGN_CENTER
        paragraph.add(childParagraph)

    }

    fun addParagrap(text:String){
        paragraph= Paragraph(text,fontText)
        paragraph.spacingAfter =5f
        paragraph.spacingBefore = 5f
        document.add(paragraph)
    }

        fun createTable(header:List<String>,reportes:List<List<String>>){
        paragraph = Paragraph()
        paragraph.font = fontFile
        var pdfTable: PdfPTable = PdfPTable(header.size)
        pdfTable.widthPercentage = 100f
        var pdfCell: PdfPCell
        var index =0
        while (index<header.size) {
            pdfCell = PdfPCell(Phrase(header[index++],fontSubtitle))
            pdfCell.horizontalAlignment = Element.ALIGN_CENTER
            pdfCell.backgroundColor = BaseColor.GRAY
            pdfTable.addCell(pdfCell) }

        for (regis in reportes){
            for(item in regis){
                context.toast(item)
             pdfTable.addCell(item)
            }
        }


            pdfTable.addCell("row 1; cell 1");
            pdfTable.addCell("row 1; cell 2");
            pdfTable.addCell("row 2; cell 1");
            pdfTable.addCell("row 2; cell 2");
            pdfTable.addCell("row 1; cell 1");
            pdfTable.addCell("row 1; cell 2");
            pdfTable.addCell("row 2; cell 1");
            pdfTable.addCell("row 2; cell 2");
            paragraph.add(pdfTable)
            document.add(paragraph)

        }

        fun  ViewPdf(){
       var intent: Intent = Intent(context,ViewPdfActivity::class.java)
        intent.putExtra("path",pdfFile.absolutePath)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

    }


}