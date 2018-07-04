package com.example.cristian.myapplication.ui.menu.vaccines


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.databinding.FragmentRevaccinationBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.VaccineAdapter
import com.example.cristian.myapplication.ui.adapters.VaccineAdapter.Companion.TYPE_NEXT_VACCINATIONS
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.*
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import javax.inject.Inject

class RevaccinationFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentRevaccinationBinding
    @Inject
    lateinit var adapter: VaccineAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    val from: Date = Date()
    val to: Date by lazy { from.add(Calendar.DATE, 7)!! }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_revaccination, container, false)
        adapter.tipo = TYPE_NEXT_VACCINATIONS
        binding.isEmpty = isEmpty
        binding.listNextVaccines.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getRevaccinations(from, to)
                .subscribeBy(
                        onNext = {
                            adapter.data = it
                            isEmpty.set(it.isEmpty())
                        }
                )



        dis add adapter.clickRevaccination
                .flatMapSingle { vacuna ->
                    val revacunacion = setRevaccination(vacuna)
                    viewModel.inserVaccine(revacunacion).flatMap {
                        viewModel.updateVaccine(vacuna.apply { proxAplicado = true })
                    }
                }.subscribe()
    }

    private fun setRevaccination(registroVacuna: RegistroVacuna): RegistroVacuna {
        val frecM = registroVacuna.frecuenciaMeses
        return registroVacuna.copy(fecha = Date(), fechaProx = Date().add(Calendar.MONTH, frecM))
    }


    companion object {
        fun instance() = RevaccinationFragment()
    }


}
