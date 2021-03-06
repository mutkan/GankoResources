package com.ceotic.ganko.ui.bovine.reproductive.add

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.models.Parto
import com.ceotic.ganko.data.models.Servicio
import com.ceotic.ganko.databinding.ActivityAddBirthBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_ID
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_POSITION
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_SERVICE
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.ceotic.ganko.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_birth.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddBirthActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    val viewModel: ReproductiveBvnViewModel by lazy { buildViewModel<ReproductiveBvnViewModel>(factory) }
    private val idBovino: String by lazy { intent.getStringExtra(ARG_ID) ?: "" }
    private val servicio: Servicio by lazy { intent.getParcelableExtra(ARG_SERVICE) as Servicio }
    private val position: Int by lazy { intent.getIntExtra(ARG_POSITION, 0) }
    private val dis: LifeDisposable = LifeDisposable(this)
    lateinit var binding: ActivityAddBirthBinding
    lateinit var bovino: Bovino
    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private val datePicker: DatePickerDialog by lazy {
        DatePickerDialog(this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }
    var ultimoParto: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_birth)
        title = "Agregar Parto"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getBovino(idBovino)
                .subscribeBy(
                        onSuccess = {
                            bovino = it
                            val numeroPartos = bovino.partos ?: 0
                            val numeroParto = numeroPartos + 1
                            binding.numeroParto = numeroParto
                        }
                )

        dis add viewModel.getLastBirthAndEmptyDays(idBovino, servicio.fecha!!)
                .subscribeBy(
                        onSuccess = {
                            binding.diasVacios = it.first
                            ultimoParto = it.second
                        }
                )

        dis add birthDate.clicks()
                .subscribeBy(
                        onNext = {
                            datePicker.show()
                        }
                )

        dis add btnCancelBirth.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )

        dis add btnAcceptBirth.clicks()
                .flatMap { validateFields() }
                .flatMapMaybe {
                    val servicio = setParto(it)
                    viewModel.addParto(idBovino, servicio, position)
                }.flatMapSingle { (bvn, srv)->viewModel.prepareNovelty(bvn, srv)
                        .flatMap { viewModel.prepareBirthZeal(bvn, srv.parto!!.fecha!!) }
                }
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )
    }

    private fun validateFields(): Observable<List<String>> {
        val fecha = birthDate.text()
        return validateForm(R.string.empty_fields, fecha)
    }

    private fun setParto(params: List<String>): Servicio {
        val fecha = params[0].toDate()
                .addCurrentHour(2)
        val sexoCria = if (calfSex.checkedRadioButtonId == R.id.male) "Macho" else "Hembra"
        val estadoCria = when (calf.checkedRadioButtonId) {
            R.id.alive -> if (sexoCria == "Macho") "Vivo" else "Viva"
            else -> if (sexoCria == "Macho") "Muerto" else "Muerta"
        }
        val intervalo = binding.intervaloPartos!!.toInt()
//        val dias = binding.diasVacios!!.toInt()
        val numero = binding.numeroParto!!
        val parto = Parto(fecha, intervalo,sexoCria, numero, estadoCria)
        return servicio.apply {
            this.finalizado = true
            this.parto = parto
        }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        birthDate.setText("$dayOfMonth/${month + 1}/$year")
        val cal = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        if (ultimoParto != null) {
            val dif = cal.timeInMillis - ultimoParto!!.time
            binding.intervaloPartos = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
        } else {
            binding.intervaloPartos = 0L
        }
    }

}
