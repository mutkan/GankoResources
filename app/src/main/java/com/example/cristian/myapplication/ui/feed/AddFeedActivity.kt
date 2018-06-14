package com.example.cristian.myapplication.ui.feed

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Ceba
import com.example.cristian.myapplication.data.models.Feed
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.bovine.ceba.AddCebaBvnActivity
import com.example.cristian.myapplication.ui.bovine.ceba.CebaViewModel
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.acitivity_add_feed.*
import kotlinx.android.synthetic.main.activity_add_ceba.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class AddFeedActivity : AppCompatActivity(),Injectable, DatePickerDialog.OnDateSetListener {


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: FeedViewModel by lazy { buildViewModel<FeedViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)

    val idBovino: String by lazy { intent.extras.getString(AddCebaBvnActivity.EXTRA_ID) }

    lateinit var datePicker: DatePickerDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_add_feed)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Agregar Alimentacion")
        datePicker = DatePickerDialog(this, AddMilkBvnActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

    }



    override fun onResume() {
        super.onResume()
        dis add btnAddFeed.clicks()
                .flatMap { validateForm(R.string.empty_fields, dateAddCebaBvn.text.toString(), weightAddCebaBvn.text.toString()) }
                .flatMapSingle {
                    viewModel.addFeed (Feed(null, null, null, "",null, idBovino, it[0].toDate(),null,null ,null))
                }
                .subscribeBy(
                        onNext = {
                            toast("Alimentacion Agregada Exitosamente")
                            finish()
                        },
                        onComplete = {
                            toast("on complete")
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )
        dis add date_feed.clicks()
                .subscribe { datePicker.show() }


        dis add btnCancelFeed.clicks()
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

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        btnAddFeed.text="$dayOfMonth/$month/$year"
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}