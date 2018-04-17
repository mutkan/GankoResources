package com.example.cristian.myapplication.ui.farms

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Finca
import com.example.cristian.myapplication.databinding.ActivityAddFarmBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.text
import com.example.cristian.myapplication.util.validateForm
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_farm.*
import javax.inject.Inject

class AddFarmActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: FarmViewModel by lazy { buildViewModel<FarmViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val userId: String = "miusuario"
    private var edit: Boolean = false
    private val farmId: String by lazy { intent.getStringExtra("farmId") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_farm)
        edit = intent.getBooleanExtra("edit", false)
        supportActionBar?.title = if (edit) "Editar Finca" else "Agregar Finca"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()

        if (edit && farmId != "") {
            dis add viewModel.getFarmById(farmId).subscribeBy(
                    onSuccess = {
                        farmName.setText(it.nombre)
                        farmLocation.setText(it.ubicacion)
                        farmHectares.setText(it.hectareas.toString())
                    }
            )
        }

        dis add btnAdd.clicks()
                .flatMap { validateForm(R.string.empty_fields, farmName.text(), farmLocation.text(), farmHectares.text()) }
                .flatMapSingle { createFarm(it) }
                .flatMapSingle { if (edit) viewModel.updateFarm(farmId, it) else viewModel.addFarm(it) }
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )
        dis add btnCancel.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )
    }

    private fun createFarm(params: List<String>): Single<Finca> {
        val nombre = params[0]
        val ubicacion = params[1]
        val hectareas = params[2].toInt()
        val finca = Finca(userId, nombre, ubicacion, hectareas)
        return Single.just(finca)
    }


}
