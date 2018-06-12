package com.example.cristian.myapplication.ui.groups

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.R

class SaveGroupActivity : AppCompatActivity() {

    val selecteds:Array<String> by lazy { intent.extras.getStringArray(DATA_BOVINES) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_group)
    }

    companion object {
        const val DATA_BOVINES = "dataBovines"
    }
}
