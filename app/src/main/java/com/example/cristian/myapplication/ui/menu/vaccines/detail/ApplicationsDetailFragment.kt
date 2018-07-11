package com.example.cristian.myapplication.ui.menu.vaccines.detail

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentApplicationsDetailBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListVaccineBovineAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


class ApplicationsDetailFragment : Fragment(), Injectable {
    // TODO: Rename and change types of parameters
    val dosisUno: String by lazy { arguments!!.getString(DOSIS_UNO) }
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    @Inject
    lateinit var adapter: ListVaccineBovineAdapter
    lateinit var binding: FragmentApplicationsDetailBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_applications_detail, container, false)
        binding.listVaccines.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dis add viewModel.getVaccinesByDosisUno(dosisUno)
                .subscribeBy(
                        onSuccess = {
                            adapter.data = it
                        }
                )

    }


    companion object {
        private const val DOSIS_UNO = "dosisUno"

        fun instance(dosisUno: String) =
                ApplicationsDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(DOSIS_UNO, dosisUno)
                    }
                }
    }
}
