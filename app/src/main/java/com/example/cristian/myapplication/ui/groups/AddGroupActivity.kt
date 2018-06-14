package com.example.cristian.myapplication.ui.groups

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.R

class AddGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)
    }

    companion object {
        const val EXTRA_GROUP = "group"
    }
}
