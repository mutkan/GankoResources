package com.ceotic.ganko.ui.bovine.vaccination

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.ActivityListVaccineBovineBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListVaccineBovineAdapter
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_vaccine_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class VaccinationBvnActivity : AppCompatActivity(), Injectable {

        @Inject
        lateinit var factory: ViewModelProvider.Factory
        val viewModel: VaccinationBvnViewModel by lazy { buildViewModel<VaccinationBvnViewModel>(factory) }
        val dis: LifeDisposable = LifeDisposable(this)
        lateinit var binding: ActivityListVaccineBovineBinding
        val isEmpty: ObservableBoolean = ObservableBoolean(false)
        @Inject
        lateinit var adapter: ListVaccineBovineAdapter

    val idBovino: String by lazy { intent.extras.getString(EXTRA_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_vaccine_bovine)
        binding.isEmpty = isEmpty
        listVaccineBovine.adapter = adapter
        title = "Vacunas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

    override fun onResume() {
        super.onResume()
        dis add viewModel.listVaccineBovine(idBovino)
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
