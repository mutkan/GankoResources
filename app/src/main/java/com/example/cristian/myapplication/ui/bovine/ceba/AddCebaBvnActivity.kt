package com.example.cristian.myapplication.ui.bovine.ceba

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Ceba
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_ceba.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class AddCebaBvnActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener, Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: CebaViewModel by lazy { buildViewModel<CebaViewModel>(factory) }

    val dis:LifeDisposable = LifeDisposable(this)

    val idBovino:String by lazy { intent.extras.getString(EXTRA_ID) }

    val datePicker = DatePickerDialog(this, AddMilkBvnActivity@ this,
            Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ceba)
    }

    override fun onResume() {
        super.onResume()
        dis add btnAddCebaBvn.clicks()
                .flatMap { validateForm(R.string.empty_fields,dateAddCebaBvn.text.toString(),weightAddCebaBvn.text.toString()) }
                .flatMapSingle {
                    viewModel.addCeba(Ceba("",idBovino,it[0].toDate(),it[1].toFloat(),0f)) }
                .subscribeBy(
                        onNext = {
                            toast("Ceba Agregada Exitosamente")
                            finish()
                        },
                        onComplete = {
                            toast("on complete")
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )
        dis add dateAddCebaBvn.clicks()
                .subscribeByAction(
                        onNext = {
                            datePicker.show()
                        },
                        onHttpError = {},
                        onError = {}
                )

        dis add btnCancelCebaBvn.clicks()
                .subscribeByAction(
                        onNext = {
                            finish()
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )
    }



    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        dateAddCebaBvn.text = "$dayOfMonth/$month/$year"
    }

    companion object {
        val EXTRA_ID:String = "idBovino"
    }
}
