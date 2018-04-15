package com.example.cristian.myapplication.ui.bovine

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class AddBovineActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: BovineViewModel by lazy { buildViewModel<BovineViewModel>(factory) }

    val dis: LifeDisposable = LifeDisposable(this)
    lateinit var idFinca: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bovine)
        idFinca = intent.getStringExtra("idFinca")
    }

    override fun onResume() {
        super.onResume()
    }



}
