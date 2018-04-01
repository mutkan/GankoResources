package com.example.cristian.myapplication.ui.bovine.milk

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Produccion
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.selected
import dagger.android.AndroidInjector
import dagger.android.HasActivityInjector
import kotlinx.android.synthetic.main.activity_add_milk_bovine.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class AddMilkBvnActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MilkBvnViewModel by lazy { buildViewModel<MilkBvnViewModel>(factory) }

    val dis: LifeDisposable = LifeDisposable(this)
    lateinit var idBovino:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_milk_bovine)
        idBovino = intent.getStringExtra("idBovino")
    }

    override fun onResume() {
        super.onResume()
        dis add btnAddMilkBovine.clicks()
                .flatMap {
                    validateForm(R.string.empty_fields, dateAddMilkBovine.text.toString(), littersAddMilkBovine.text())
                }
                .flatMap {
                    val jornada = if (timeOfDayAddMilkBovine.checkedRadioButtonId == R.id.morningAddMilkBovine) "Mañana"
                    else "Tarde"
                    viewModel.addMilkProduction(idBovino,
                            Produccion("", idBovino, jornada, it[1], it[0].toDate()))
                }.subscribeByAction(
                        onNext = {
                            toast("Produccion de Leche Agregada")
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )
        dis add dateAddMilkBovine.clicks()
                .subscribeByAction(
                        onNext = {
                            val datePicker = DatePickerDialog(this, AddMilkBvnActivity@ this, 2018, 3, 12).show()
                        },
                        onHttpError = {},
                        onError = {}
                )
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        dateAddMilkBovine.text = "$p3/$p2/$p1"
    }
}
