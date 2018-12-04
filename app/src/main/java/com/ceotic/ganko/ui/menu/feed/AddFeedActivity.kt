package com.ceotic.ganko.ui.menu.feed

import android.app.Activity
import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Group
import com.ceotic.ganko.data.models.RegistroAlimentacion
import com.ceotic.ganko.data.models.toGrupo
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.bovine.ceba.AddCebaBvnActivity
import com.ceotic.ganko.ui.groups.GroupFragment
import com.ceotic.ganko.ui.groups.SelectActivity
import com.ceotic.ganko.util.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.itemSelections
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.acitivity_add_feed.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class AddFeedActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: FeedViewModel by lazy { buildViewModel<FeedViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)

    val idBovino: String by lazy { intent.extras.getString(AddCebaBvnActivity.EXTRA_ID) }
    var group: Group? = null
    var bovines: List<String>? = null
    var alimento: String = ""
    var groupFragment: GroupFragment? = null
    val calendar: Calendar by lazy { Calendar.getInstance() }
    val tipo_alimento: Array<String> by lazy { resources.getStringArray(R.array.feed_types) }
    lateinit var datePicker: DatePickerDialog




    var precioTotal: Int = 0
        set(value) {
            field = value
            total_price_feed.text = "$$value"
        }
    var kg: Int = 0
        set(value) {
            field = value
            precioTotal = precioKg * value
        }
    var precioKg: Int = 0
        set(value) {
            field = value
            precioTotal = kg * value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivity_add_feed)
        fixColor(9)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.addFeed)

        startActivityForResult<SelectActivity>(SelectActivity.REQUEST_SELECT,
                SelectActivity.EXTRA_COLOR to 4)

        datePicker= DatePickerDialog(this, AddFeedActivity@ this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))

        onDateSet(null, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

    }

    fun setupGroupFragment() {
        if ((group != null || bovines != null) && groupFragment == null) {
            groupFragment = if (group != null) GroupFragment.instance(4, group!!)
            else GroupFragment.instance(4, bovines!!)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.feedContainer, groupFragment)
                    .commit()

            dis add groupFragment!!.ids
                    .filter { group == null }
                    .subscribe { bovines = it }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setupGroupFragment()

        dis add food_type.itemSelections()
                .subscribeBy(
                        onNext = {
                            alimento = tipo_alimento[it]
                        }
                )



        dis add btnAddFeed.clicks()
                .flatMap { validateFields() }
                .flatMapSingle {
                    val alimentacion = createFeed(it)
                    viewModel.addFeed(alimentacion)
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
                .subscribeBy(
                        onNext = {
                            datePicker.show()
                        })

        dis add price_feed.textChanges()
                .subscribeBy(
                        onNext = {
                            val value = it.toString()
                            precioKg = if (value != "") value.toInt() else 0
                        }
                )

        dis add ration_feed.textChanges()
                .subscribeBy(
                        onNext = {
                            val value = it.toString()
                            kg = if (value != "") value.toInt() else 0
                        }
                )


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
        val sDate = "$dayOfMonth/${month + 1}/$year"
        date_feed.setText(sDate)

    }

    private fun validateFields(): Observable<List<String>> {
        val fecha = date_feed.text()
        val peso = ration_feed.text()
        val valorkg = price_feed.text()
        return validateForm(R.string.empty_fields, fecha, peso, valorkg)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SelectActivity.REQUEST_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                group = data?.extras?.getParcelable(SelectActivity.DATA_GROUP)
                bovines = data?.extras?.getStringArray(SelectActivity.DATA_BOVINES)?.toList()
            } else {
                finish()
            }
        }
    }

    private fun createFeed(fields: List<String>): RegistroAlimentacion {
        val fecha = fields[0].toDate()
        val racion = fields[1].toInt()
        val precio_kg = fields[2].toInt()
        val precio_total = precioTotal
        val idFinca = viewModel.getFarmId()
        return when (alimento) {
            "Forraje" -> RegistroAlimentacion(idFinca = idFinca, tipoAlimento = "Forraje", bovinos = bovines, fecha = fecha, peso = racion, valorkg = precio_kg, valorTotal = precio_total, grupo = group?.toGrupo())
            "Concentrado" -> RegistroAlimentacion(idFinca = idFinca, tipoAlimento = "Concentrado", bovinos = bovines, fecha = fecha, peso = racion, valorkg = precio_kg, valorTotal = precio_total, grupo = group?.toGrupo())
            else -> RegistroAlimentacion(idFinca = idFinca, tipoAlimento = "Maiz", bovinos = bovines, fecha = fecha, peso = racion, valorkg = precio_kg, valorTotal = precio_total, grupo = group?.toGrupo())

        }
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}