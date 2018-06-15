package com.example.cristian.myapplication.ui.bovine

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.ActivityAddBovineBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.itemSelections
import com.squareup.picasso.Picasso
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_bovine.*
import org.jetbrains.anko.toast
import java.io.File
import java.util.*
import javax.inject.Inject


class AddBovineActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, Injectable {


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: BovineViewModel by lazy { buildViewModel<BovineViewModel>(factory) }
    private val farmId by lazy { viewModel.getFarmId() }
    lateinit var binding: ActivityAddBovineBinding
    val dis: LifeDisposable = LifeDisposable(this)
    lateinit var datePicker: DatePickerDialog
    var foto: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bovine)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.add_bovine)
        binding.page = 1
        datePicker = DatePickerDialog(this, AddBovineActivity@ this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    }

    override fun onResume() {
        super.onResume()

        dis add addBovinePhoto.clicks()
                .flatMap { PhotoUtil.captureImage(this) }
                .subscribe()

        dis add PhotoUtil.processedImg
                .subscribe {
                    //Nombre de Archivo
                    Log.i("SIIIII", "YA FUNCA")
                    foto = it
                    Picasso.get().load(it)
                            .into(imgBovino)
                }


        dis add sexBovine.checkedChanges()
                .subscribe {
                    binding.stateSex = male.isChecked
                }

        dis add check_weaned.checkedChanges()
                .subscribeBy(
                        onNext = {
                            binding.stateCheckBox = it
                        }
                )

        dis add spinnerOrigin.itemSelections()
                .subscribeBy(
                        onNext = {
                            binding.stateOrigin = it == 1
                        }
                )

        dis add bovineBirthDate.clicks()
                .subscribeByAction(
                        onNext = {
                            datePicker.datePicker.tag = "birthdate"
                            datePicker.show()
                        },
                        onError = {},
                        onHttpError = {}
                )

        dis add bovineWeanedDate.clicks()
                .subscribeByAction(
                        onNext = {
                            datePicker.datePicker.tag = "weaneddate"
                            datePicker.show()
                        },
                        onError = {},
                        onHttpError = {}
                )

        dis add purchaseDate.clicks()
                .subscribeByAction(
                        onNext = {
                            datePicker.datePicker.tag = "purchasedate"
                            datePicker.show()
                        },
                        onError = {},
                        onHttpError = {}
                )

        dis add btnFinalize.clicks()
                .flatMapSingle {
                    val sex = if (sexBovine.checkedRadioButtonId == R.id.male) "Macho"
                    else "Hembra"
                    val purpose = when {
                        purpose.checkedRadioButtonId == R.id.dairy -> "Lecheria"
                        purpose.checkedRadioButtonId == R.id.meat -> "Ceba"
                        else -> "Ambos"
                    }
                    viewModel.addBovine(Bovino(null, null, null, "", bovineIdentificationNumber.text(), foto,
                            bovineName.text(), bovineBirthDate.text.toString().toDate(),
                            if (spinnerOrigin.selectedItem.toString() == "Compra") purchaseDate.text.toString().toDate() else null
                            , sex, purpose,
                            bovineWeight.text().toInt(), bovineColor.text(), bovineRace.text(), motherId.text(), fatherId.text(),
                            null,
                            if (male.isChecked) null else previousBovineBirths.text().toInt(),
                            if (spinnerOrigin.selectedItem.toString() == "Compra") purchasePrice.text().toInt() else null
                            , spinnerOrigin.selectedItem.toString(), false, null, null,
                            null, farmId, check_weaned.isChecked,
                            if (check_weaned.isChecked) {
                                bovineWeanedDate.text.toString().toDate()
                            } else null,
                            null, null, listOf(), listOf(), listOf()))
                }.subscribeBy(
                        onNext = {
                            toast("Bovino agregado exitosamente")
                            finish()
                        },
                        onComplete = {
                            toast("onComplete")
                        },
                        onError = {
                            toast(it.message!! + "SI AQUI")
                        }
                )

        dis add btnAddFeed.clicks()
                .flatMap {
                    if (binding.page == 1)
                        validateForm(R.string.empty_fields, bovineIdentificationNumber.text.toString(),
                                bovineName.text.toString(), bovineRace.text.toString(), spinnerOrigin.selectedItem.toString(),
                                if (sexBovine.checkedRadioButtonId == R.id.male) "Macho"
                                else "Hembra",
                                if (sexBovine.checkedRadioButtonId == R.id.male) "No Previous Births"
                                else previousBovineBirths.text.toString()

                        )
                    else if (binding.page == 2) {
                        val bvnWeanedDate: String
                        if (check_weaned.isChecked) bvnWeanedDate = bovineWeanedDate.text.toString()
                        else bvnWeanedDate = "NoWeaned"
                        validateForm(R.string.empty_fields, bovineWeight.text.toString(), bovineColor.text.toString(),
                                bovineBirthDate.text.toString(), bvnWeanedDate,
                                when {
                                    purpose.checkedRadioButtonId == R.id.dairy -> "Lecheria"
                                    purpose.checkedRadioButtonId == R.id.meat -> "Ceba"
                                    else -> "Ambos"
                                }
                        )
                    } else {
                        validateForm(R.string.empty_fields,
                                if (spinnerOrigin.selectedItem == "Compra") purchaseDate.text.toString()
                                else "No Aplica",
                                if (spinnerOrigin.selectedItem == "Compra") purchasePrice.text.toString()
                                else "No Aplica"
                        )
                    }
                }
                .subscribeBy(
                        onNext = {
                            plusPage()
                        }

                )

        dis add btnBack.clicks()
                .subscribeBy(
                        onNext = {
                            minusPage()
                        }
                )

        dis add btnCancelFeed.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        when (view!!.tag) {
            "birthdate" -> bovineBirthDate.text = "$dayOfMonth/${month + 1}/$year"
            "weaneddate" -> bovineWeanedDate.text = "$dayOfMonth/${month + 1}/$year"
            "purchasedate" -> purchaseDate.text = "$dayOfMonth/${month + 1}/$year"
        }
        //    dateAddMilkBovine.text = "$dayOfMonth/$month/$year"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        PhotoUtil.processImage(this, requestCode, resultCode, 800, 800, data)
    }

    private fun plusPage() {
        binding.page = binding.page!!.plus(1)
    }

    private fun minusPage() {
        binding.page = binding.page!!.minus(1)
    }


}
