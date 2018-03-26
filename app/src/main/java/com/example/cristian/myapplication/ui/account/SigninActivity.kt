package com.example.cristian.myapplication.ui.account

import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

class SigninActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel:AccountViewModel by lazy { buildViewModel<AccountViewModel>(factory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
