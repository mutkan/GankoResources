package com.example.cristian.myapplication.ui.bovine.health

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.HealthBovineAdapter
import com.example.cristian.myapplication.util.buildViewModel
import kotlinx.android.synthetic.main.activity_health_bovine.*
import javax.inject.Inject

class HealthBvnActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: HealthBvnViewModel by lazy { buildViewModel<HealthBvnViewModel>(factory) }

    @Inject
    lateinit var adapter: HealthBovineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_bovine)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler.adapter = adapter
    }
}
