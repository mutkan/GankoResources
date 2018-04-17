package com.example.cristian.myapplication.ui.bovine

import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

class DetailBovineActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: BovineViewModel by lazy { buildViewModel<BovineViewModel>(factory) }

    //val bovine: Bovino by lazy { intent.extras.getParcelable()<Bovino>(EXTRA_BOVINE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    companion object {
        val EXTRA_BOVINE = "bovine"
    }
}
