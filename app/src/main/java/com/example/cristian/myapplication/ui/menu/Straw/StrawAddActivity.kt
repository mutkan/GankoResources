package com.example.cristian.myapplication.ui.menu.Straw

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Straw
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_straw.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class StrawAddActivity : AppCompatActivity(), Injectable, AdapterView.OnItemSelectedListener{


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: StrawViewModel by lazy { buildViewModel<StrawViewModel>(factory) }
    val menuViewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    private val farmId by lazy { menuViewModel.getFarmId() }
    val dis: LifeDisposable = LifeDisposable(this)

    val idBovino: String by lazy { intent.extras.getString(StrawAddActivity.EXTRA_ID) }
    lateinit var spinner: Spinner
    var selectedItem: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_straw)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(getString(R.string.add_straw))
        spinner = type_spinner
        spinner.onItemSelectedListener
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedItem = spinner.selectedItem.toString()
    }

    override fun onResume() {
        super.onResume()

        dis add btnAdd.clicks()
                .flatMap {
                    validateForm(R.string.empty_fields, strawId.text.toString(), layette.text.toString(),
                           bull.text.toString(), origin.text.toString(), value.text.toString())
                }
                .flatMapSingle {
                    viewModel.addStraw(
                            Straw(farmId, it[0], selectedItem, it[1], null, null, it[2], it[3], it[4], null))
                }.subscribeBy(
                        onComplete = {
                            toast("Completo")
                        },
                        onNext = {
                            toast("Entrada agregada exitosamente"+it)
                            finish()

                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add  btnCancel.clicks()
                .subscribe{
                    finish()
                }

    }
    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
