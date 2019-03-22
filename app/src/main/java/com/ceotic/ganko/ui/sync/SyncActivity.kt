package com.ceotic.ganko.ui.sync

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.App
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.ActivitySyncBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.fixColor
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_sync.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class SyncActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: ActivitySyncBinding
    val dis: LifeDisposable = LifeDisposable(this)
    val viewModel: SyncViewModel by lazy { buildViewModel<SyncViewModel>(factory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sync)
        fixColor(2)
    }

    override fun onResume() {
        super.onResume()

        dis add btnSync.clicks()
                .map { viewModel.validatePlan() }
                .doOnNext { if(!it) toast(R.string.plan_date_limit) }
                .filter { it }
                .doOnNext {
                    binding.sync = true
                    (application as App).stopReplicator()
                }
                .flatMap { viewModel.replicate(getString(R.string.url_sync)) }
                .subscribe { syncState(it) }
    }


    private fun syncState(state:Int){
        when(state){
            SyncViewModel.STOPPED -> {
                binding.sync = false
                toast(getString(R.string.sync_success))
            }

            SyncViewModel.OFFLINE->{
                binding.sync = false
                toast(getString(R.string.sync_offline))
            }

            SyncViewModel.ERROR ->{
                binding.sync = false
                toast(R.string.sync_error)
            }
        }

        (application as App).startReplicator()
    }
}
