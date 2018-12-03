package com.ceotic.ganko.ui.menu.straw

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Straw
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.*
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_add_health.*
import kotlinx.android.synthetic.main.activity_add_straw.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class StrawAddActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: StrawViewModel by lazy { buildViewModel<StrawViewModel>(factory) }
    val menuViewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    private val farmId by lazy { menuViewModel.getFarmId() }
    val dis: LifeDisposable = LifeDisposable(this)
    lateinit var datePicker: DatePickerDialog

    val idBovino: String by lazy { intent.extras.getString(StrawAddActivity.EXTRA_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_straw)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_straw)
        fixColor(9)

        datePicker = DatePickerDialog(this, AddStrawActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        onDateSet(null, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        dis add strawDate.clicks()
                .subscribe { datePicker.show() }

        dis add btnAdd.clicks()
                .flatMap {
                    validateForm(R.string.empty_fields, strawId.text.toString(), layette.text.toString(),
                            bull.text.toString(), origin.text.toString(), value.text.toString(), breed.text.toString(), strawDate.text.toString())
                }
                .flatMapSingle {
                    viewModel.addStraw(
                            Straw(null, null, null, farmId, strawDate.text.toString().toDate(), it[0], it[1], it[5], spinner.selectedItem.toString(), it[2], it[3], it[4], Straw.UNUSED_STRAW))
                }
                .doOnError { toast(R.string.straw_exist) }
                .retry()
                .subscribe {
                    toast("Pajilla agregada exitosamente")
                    finish()
                }

        dis add btnCancelFeed.clicks()
                .subscribe {
                    finish()
                }

    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        strawDate.setText("$dayOfMonth/${month + 1}/$year")
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
