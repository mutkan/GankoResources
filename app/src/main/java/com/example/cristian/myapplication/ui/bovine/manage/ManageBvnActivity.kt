package com.example.cristian.myapplication.ui.bovine.manage

import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.bovine.health.HealthBvnViewModel
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

class ManageBvnActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ManageBvnViewModel by lazy { buildViewModel<ManageBvnViewModel>(factory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_bovine)
    }
}
