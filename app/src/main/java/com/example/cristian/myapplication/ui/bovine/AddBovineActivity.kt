package com.example.cristian.myapplication.ui.bovine

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

class AddBovineActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: BovineViewModel by lazy { buildViewModel<BovineViewModel>(factory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
