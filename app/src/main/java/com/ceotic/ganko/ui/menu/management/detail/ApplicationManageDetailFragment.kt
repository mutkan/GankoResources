package com.ceotic.ganko.ui.menu.management.detail


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.FragmentApplicationManageDetailBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListManagementBovineAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable

import com.ceotic.ganko.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ApplicationManageDetailFragment : Fragment(), Injectable {

    val dosisUno: String by lazy { arguments!!.getString(DOSIS_UNO) }
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: MenuViewModel by  lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    @Inject
    lateinit var adapter: ListManagementBovineAdapter
    lateinit var binding: FragmentApplicationManageDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_application_manage_detail, container, false)
        binding.recyclerListManage.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getManagesByDosisUno(dosisUno)
                .subscribeBy(
                        onSuccess = {
                            adapter.data = it
                        }
                )

    }

    companion object {
        private const val DOSIS_UNO = "dosisUno"

        fun instance(dosisUno: String) =
                ApplicationManageDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(DOSIS_UNO, dosisUno)
                    }
                }
    }


}
