package com.ceotic.ganko.ui.menu.vaccines


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.FragmentRecentVaccinesBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.VaccineAdapter
import com.ceotic.ganko.ui.adapters.VaccineAdapter.Companion.TYPE_VACCINATIONS
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.ui.menu.vaccines.detail.VaccineDetailActivity
import com.ceotic.ganko.ui.menu.vaccines.detail.VaccineDetailActivity.Companion.ID_FIRST_VACCINE
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_recent_vaccines.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject

class RecentVaccinesFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentRecentVaccinesBinding
    @Inject
    lateinit var adapter: VaccineAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val isEmpty: ObservableBoolean = ObservableBoolean(false)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recent_vaccines, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.isEmpty = isEmpty
        binding.vaccinationsList.adapter = adapter.apply { tipo = TYPE_VACCINATIONS }
    }

    override fun onResume() {
        super.onResume()

        dis add adapter.clickVacuna
                .subscribeBy(
                        onNext = {
                            startActivity<VaccineDetailActivity>(VaccineDetailActivity.ID_VACCINE to it._id!!, ID_FIRST_VACCINE to it.idAplicacionUno!!)
                        }
                )

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
        fun instance() = RecentVaccinesFragment()
    }


}
