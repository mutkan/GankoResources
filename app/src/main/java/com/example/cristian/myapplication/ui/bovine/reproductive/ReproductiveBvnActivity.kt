package com.example.cristian.myapplication.ui.bovine.reproductive

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.ActivityReproductiveBovineBinding

class ReproductiveBvnActivity : AppCompatActivity() {

    lateinit var binding:ActivityReproductiveBovineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reproductive_bovine)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
