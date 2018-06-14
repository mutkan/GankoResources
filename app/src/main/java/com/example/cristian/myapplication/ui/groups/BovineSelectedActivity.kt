package com.example.cristian.myapplication.ui.groups

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.R

class BovineSelectedActivity : AppCompatActivity() {

    val selecteds:HashMap<String, Boolean> by lazy {
        intent.extras.getSerializable(EXTRA_SELECTED) as HashMap<String, Boolean>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bovine_selected)
    }

    companion object {
        const val EXTRA_SELECTED = "selected"

    }
}
