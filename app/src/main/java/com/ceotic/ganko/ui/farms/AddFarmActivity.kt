package com.ceotic.ganko.ui.farms

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Finca
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.text
import com.ceotic.ganko.util.validateForm
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_farm.*
import javax.inject.Inject

class AddFarmActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: FarmViewModel by lazy { buildViewModel<FarmViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val userId: String by lazy { viewModel.getUserId() }
    private var edit: Boolean = false
    private lateinit var farm: Finca

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_farm)
        edit = intent.getBooleanExtra("edit", false)
        if (edit) {
            farm = intent.getParcelableExtra("farm")
            farmName.setText(farm.nombre)
            farmLocation.setText(farm.ubicacion)
            farmHectares.setText(farm.hectareas.toString())
            btnAdd.text = getString(R.string.save)
        }
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

        dis add btnAdd.clicks()
                .flatMap { validateForm(R.string.empty_fields, farmName.text(), farmLocation.text(), farmHectares.text()) }
                .flatMapSingle {
                    val finca = createFarm(it)
                    if (edit) viewModel.updateFarm(farm._id!!, finca) else viewModel.addFarm(finca)
                }
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )
        dis add btnCancelFeed.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )
    }

    private fun createFarm(params: List<String>): Finca {
        val nombre = params[0]
        val ubicacion = params[1]
        val hectareas = params[2].toInt()
        return if (edit) {
            farm.apply {
                this.nombre = nombre
                this.ubicacion = ubicacion
                this.hectareas = hectareas
            }
        } else {
            Finca(userId, nombre, ubicacion, hectareas)
        }

    }


}
