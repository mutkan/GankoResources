package com.example.cristian.myapplication.ui.bovine.vaccination

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.couchbase.lite.internal.support.Log
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListVaccineBovineAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_vaccine_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class VaccinationBvnActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: VaccinationBvnViewModel by lazy { buildViewModel<VaccinationBvnViewModel>(factory) }

    val dis:LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var adapter:ListVaccineBovineAdapter

    val idBovino:String by lazy { intent.extras.getString(EXTRA_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_vaccine_bovine)
        listVaccineBovine.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        dis add viewModel.listVaccineBovine(idBovino)
                .subscribeBy (
                        onSuccess = {
                            adapter.data = it
                        },
                        onComplete = {
                            Log.i("VACINE","On complete vaccine bovine")
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
