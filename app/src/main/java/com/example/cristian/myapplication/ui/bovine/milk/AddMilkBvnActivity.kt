package com.example.cristian.myapplication.ui.bovine.milk

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

class AddMilkBvnActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MilkBvnViewModel by lazy { buildViewModel<MilkBvnViewModel>(factory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
