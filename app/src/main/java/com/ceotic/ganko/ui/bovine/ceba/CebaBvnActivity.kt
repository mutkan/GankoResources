package com.ceotic.ganko.ui.bovine.ceba

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.DatePicker
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.databinding.ActivityListCebaBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListCebaBovineAdapter
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.toDate
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_ceba.*
import org.jetbrains.anko.*
import java.util.*
import javax.inject.Inject

class CebaBvnActivity : AppCompatActivity(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: CebaViewModel by lazy { buildViewModel<CebaViewModel>(factory) }

    @Inject
    lateinit var cebaAdapter: ListCebaBovineAdapter

    private val dis: LifeDisposable = LifeDisposable(this)
    lateinit var binding: ActivityListCebaBinding

    val idBovino: String by lazy { intent.extras.getString(EXTRA_ID) }
    lateinit var datePicker: DatePickerDialog
    lateinit var bovino: Bovino
    var first = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_list_ceba)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Ceba")
        recyclerListCeba.adapter = cebaAdapter
        recyclerListCeba.layoutManager = LinearLayoutManager(this)

        datePicker = DatePickerDialog(this, AddMilkBvnActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

        onDateSet(null, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

    }

    override fun onResume() {
        super.onResume()
        getCeba()
        dis add viewModel.getBovineInfo(idBovino)
                .subscribeBy(
                        onSuccess = {
                            bovino = it
                            if (it.destete!!) binding.fecha = it.fechaDestete
                            else desteteListCebaBovine.text = "Sin fecha de destete"
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add cebaAdapter.onClickDeleteCeba
                .subscribeBy { ceba ->
                    alert {
                        title = "¿Desea eliminar este registro?"
                        yesButton {
                            ceba.eliminado = true
                            dis add viewModel.safeDeleteCeba(ceba._id!!, ceba)
                                    .subscribeBy(
                                            onSuccess = {
                                                getCeba()
                                                toast("Datos actualizados")
                                            },
                                            onError = {
                                                toast(it.message!!)
                                            }
                                    )
                        }
                        noButton { }
                    }.show()
                }

        dis add editDestete.clicks()
                .subscribe {
                    datePicker.show()
                }


        dis add btnGoToAddCeba.clicks()
                .subscribe {
                    startActivity<AddCebaBvnActivity>("idBovino" to idBovino)
                }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun getCeba(){
        dis add viewModel.getListCeba(idBovino)
                .subscribeBy(
                        onSuccess = {
                            cebaAdapter.data = it
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        desteteListCebaBovine.text = "$dayOfMonth/${month + 1}/$year"
        if (!first) {
            bovino.fechaDestete = "$dayOfMonth/${month + 1}/$year".toDate()
            bovino.destete = true
            dis add viewModel.updateBovine(idBovino, bovino)
                    .subscribeBy(
                            onSuccess = {
                                desteteListCebaBovine.text = "$dayOfMonth/${month + 1}/$year"
                                toast("Datos actualizados")
                            },
                            onError = {
                                toast(it.message!!)
                            }
                    )
        }
        first = false
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
