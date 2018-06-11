package com.example.cristian.myapplication.ui.groups

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.R

class SaveGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_group)
    }

    companion object {

        val DATA_BOVINES = "dataBovines"

    }
}
