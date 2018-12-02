package com.ceotic.ganko.ui.farms

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Finca
import com.ceotic.ganko.databinding.ActivityListFarmBinding
import com.ceotic.ganko.databinding.TemplateAlertDeleteFarmBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListFarmAdapter
import com.ceotic.ganko.ui.menu.MenuActivity
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.text
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_farm.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class FarmActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: FarmViewModel by lazy { buildViewModel<FarmViewModel>(factory) }
    @Inject
    lateinit var listFarmAdapter: ListFarmAdapter
    val dis: LifeDisposable = LifeDisposable(this)
    private val mAlert: AlertDialog by lazy { AlertDialog.Builder(this).create() }
    private lateinit var alertBinding: TemplateAlertDeleteFarmBinding
    private lateinit var binding: ActivityListFarmBinding
    val isEmpty: ObservableBoolean = ObservableBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_farm)
        binding.isEmpty = isEmpty
        supportActionBar?.title = "Fincas"
//        userId = viewModel.getUserId()
        listFarmActivity.adapter = listFarmAdapter
        createAlert()
    }

    override fun onResume() {
        super.onResume()
        dis add viewModel.getAllByUser()
                .subscribeBy(
                        onNext = {
                            isEmpty.set(it.isEmpty())
                            listFarmAdapter.farms = it
                        }
                )

        dis add fabAddFarm.clicks()
                .subscribeBy(
                        onNext = {
                            startActivity<AddFarmActivity>("edit" to false)
                        }
                )
        dis add listFarmAdapter.clickEditFarm
                .subscribeBy(
                        onNext = {
                            startActivity<AddFarmActivity>("edit" to true, "farm" to it)
                        }
                )
        dis add listFarmAdapter.clickDeleteFarm
                .flatMap { finca -> showAlert().map { finca } }
                .flatMap { finca -> alertBinding.btnAccept.clicks().map { finca } }
                .flatMap { validateName(it, alertBinding.farmName.text()) }
                .flatMapSingle { finca -> viewModel.deleteFarm(finca._id!!).map { finca } }
                .map { toast("${it.nombre} eliminada") }
                .subscribe()

        dis add alertBinding.btnCancel.clicks().subscribeBy(
                onNext = {
                    mAlert.cancel()
                    alertBinding.farmName.setText("")
                }
        )
        dis add listFarmAdapter.clickFarm
                .flatMap { viewModel.setFarm(it._id!!, it.nombre) }
                .subscribeBy(
                        onNext = {
                            startActivity<MenuActivity>()
                            finish()
                        }
                )
    }

    private fun validateName(finca: Finca, nombre: String): Observable<Finca> = Observable.create<Finca> { e ->
        if (finca.nombre == nombre) {
            e.onNext(finca)
            mAlert.dismiss()
            alertBinding.farmName.setText("")
        } else {
            toast("Nombre no coincide")
        }
    }

    override fun onStop() {
        super.onStop()
        mAlert.cancel()
    }

    private fun showAlert(): Observable<Unit> = Observable.fromCallable {
        mAlert.show()
    }


    private fun createAlert() {
        alertBinding = DataBindingUtil.inflate(layoutInflater, R.layout.template_alert_delete_farm, null, false)
        val mView = alertBinding.root
        mAlert.setView(mView)
        mAlert.setCancelable(false)
    }
}
