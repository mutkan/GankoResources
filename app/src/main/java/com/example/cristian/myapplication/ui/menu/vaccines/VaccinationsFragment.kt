package com.example.cristian.myapplication.ui.menu.vaccines


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentVaccinationsBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.VaccineAdapter
import com.example.cristian.myapplication.ui.adapters.VaccineAdapter.Companion.TYPE_VACCINATIONS
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_vaccinations.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 *
 */
class VaccinationsFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentVaccinationsBinding
    @Inject
    lateinit var adapter: VaccineAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val isEmpty: ObservableBoolean = ObservableBoolean(false)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vaccinations, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.isEmpty = isEmpty
        binding.vaccinationsList.adapter = adapter.apply { tipo = TYPE_VACCINATIONS }
    }

    override fun onResume() {
        super.onResume()
        dis add viewModel.getVaccinations()
                .subscribeBy(
                        onNext = {
                            Log.d("VACUNAS", it.toString())
                            isEmpty.set(it.isEmpty())
                            adapter.data = it
                        }
                )
        dis add fabAddVaccination.clicks()
                .subscribeBy(
                        onNext = {
                            startActivity<AddVaccineActivity>()
                        }
                )
    }


    companion object {
        fun instance() = VaccinationsFragment()
    }


}
