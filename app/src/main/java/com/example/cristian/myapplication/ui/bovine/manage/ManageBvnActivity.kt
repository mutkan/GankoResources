package com.example.cristian.myapplication.ui.bovine.manage

import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ManageBovineAdapter
import com.example.cristian.myapplication.ui.bovine.health.HealthBvnViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByShot
import kotlinx.android.synthetic.main.activity_manage_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class ManageBvnActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ManageBvnViewModel by lazy { buildViewModel<ManageBvnViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var adapter: ManageBovineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_bovine)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        dis add  viewModel.getManageBovine()
                .subscribeByShot(
                        onNext = {
                            adapter.manage = it
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
