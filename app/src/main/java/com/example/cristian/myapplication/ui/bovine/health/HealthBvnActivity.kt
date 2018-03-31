package com.example.cristian.myapplication.ui.bovine.health

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.HealthBovineAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByShot
import kotlinx.android.synthetic.main.activity_list_health_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class HealthBvnActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: HealthBvnViewModel by lazy { buildViewModel<HealthBvnViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var adapter: HealthBovineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_health_bovine)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerListHealthBovine.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getHealthBovine()
                .subscribeByShot(
                        onNext = {
                            adapter.health = it
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )
    }
}
