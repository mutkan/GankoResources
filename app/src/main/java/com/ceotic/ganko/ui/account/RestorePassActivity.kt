package com.ceotic.ganko.ui.account

import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.util.buildViewModel
import javax.inject.Inject

class RestorePassActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel:AccountViewModel by lazy { buildViewModel<AccountViewModel>(factory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
