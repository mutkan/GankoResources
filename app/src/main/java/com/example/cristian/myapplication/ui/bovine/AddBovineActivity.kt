package com.example.cristian.myapplication.ui.bovine

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.DatePicker
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.ActivityAddBovineBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.text
import com.example.cristian.myapplication.util.validateForm
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_bovine.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject


class AddBovineActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, Injectable {


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: BovineViewModel by lazy { buildViewModel<BovineViewModel>(factory) }

    lateinit var binding: ActivityAddBovineBinding

    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bovine)
        binding.page = 1
    }

    override fun onResume() {
        super.onResume()

        dis add check_weaned.checkedChanges()
                .subscribeBy (
                        onNext = {
                            binding.stateCheckBox = it
                        }
                )

        dis add btnFinalize.clicks()
                .flatMap { validateForm(R.string.empty_fields, fatherId.text.toString(), motherId.text.toString()) }
                //.flatMap { Bovino(null,bovineIdentificationNumber.text(),null, null,
                //        bovineName.text(),bovineBirthDate.text(),)}
                .subscribeBy(
                        onNext = {
                            toast(it.toString())
                        }
                )

        dis add btnNext.clicks()
                .flatMap {
                    if (binding.page == 1)
                        validateForm(R.string.empty_fields, bovineIdentificationNumber.text.toString(),
                                bovineName.text.toString(), bovineRace.text.toString(),
                                if (sexBovine.checkedRadioButtonId == R.id.male) "Macho"
                                else "Hembra",
                                previousBovineBirths.text.toString())
                    else {
                        val bvnWeanedDate: String
                        if(check_weaned.isChecked) bvnWeanedDate = bovineWeanedDate.text.toString()
                        else bvnWeanedDate = "NoWeaned"
                        validateForm(R.string.empty_fields, bovineWeight.text.toString(), bovineColor.text.toString(),
                                bovineBirthDate.text.toString(),bvnWeanedDate,
                                when {
                                    purpose.checkedRadioButtonId == R.id.dairy -> "Lecheria"
                                    purpose.checkedRadioButtonId == R.id.meat -> "Ceba"
                                    else -> "Ambos"
                                }
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

        dis add btnCancel.clicks()
                .subscribeBy(
                        onNext = {
                            finish()
                        }
                )

    }

    private fun plusPage() {
        binding.page = binding.page!!.plus(1)
    }

    private fun minusPage() {
        binding.page = binding.page!!.minus(1)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        when (view!!.id){

        }
    //    dateAddMilkBovine.text = "$dayOfMonth/$month/$year"
    }


}
