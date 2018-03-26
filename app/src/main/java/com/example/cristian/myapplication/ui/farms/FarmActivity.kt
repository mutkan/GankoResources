package com.example.cristian.myapplication.ui.farms

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

class FarmActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: FarmViewModel by lazy { buildViewModel<FarmViewModel>(factory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
