package com.ceotic.ganko.pdf

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ceotic.ganko.R
import kotlinx.android.synthetic.main.activity_view_pdf.*
import java.io.File

class ViewPdfActivity : AppCompatActivity() {
    lateinit var file : File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pdf)

        if (intent.extras!=null) file = File(intent.extras.getString("path",""))


        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load() }

}
