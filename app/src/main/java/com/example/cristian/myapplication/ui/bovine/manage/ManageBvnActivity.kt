package com.example.cristian.myapplication.ui.bovine.manage

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.ActivityListManagementBovineBinding

import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListManagementBovineAdapter

import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel

import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_management_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class ManageBvnActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ManageBvnViewModel by lazy { buildViewModel<ManageBvnViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    lateinit var binding: ActivityListManagementBovineBinding
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    val idBovino:String by lazy{ intent.extras.getString(EXTRA_ID) }
    @Inject
    lateinit var adapter: ListManagementBovineAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_management_bovine)
        binding.isEmpty = isEmpty
        recyclerListManageBovine.adapter = adapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setTitle("Manejo")

    }

    override fun onResume() {
        super.onResume()

        dis add  viewModel.listManageBovine(idBovino)
                .subscribeBy(
                        onSuccess = {
                            adapter.data = it
                            isEmpty.set(it.isEmpty())
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
