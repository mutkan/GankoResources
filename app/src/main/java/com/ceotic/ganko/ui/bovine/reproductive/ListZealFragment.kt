package com.ceotic.ganko.ui.bovine.reproductive

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.FragmentListZealBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListZealAdapter
import com.ceotic.ganko.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_ID
import com.ceotic.ganko.ui.bovine.reproductive.add.AddServiceActivity
import com.ceotic.ganko.ui.bovine.reproductive.add.AddZealActivity
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.toStringFormat
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_list_zeal.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject

class ListZealFragment : Fragment(), Injectable {

    lateinit var binding: FragmentListZealBinding
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var listZealAdapter: ListZealAdapter
    val viewModel: ReproductiveBvnViewModel by lazy { buildViewModel<ReproductiveBvnViewModel>(factory) }
    val dis: LifeDisposable by lazy { LifeDisposable(this) }
    val idBovino: String by lazy { arguments!!.getString(ID_BOVINO) }
    val isEmpty: ObservableBoolean = ObservableBoolean(false)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_zeal, container, false)
        binding.listZeal.adapter = listZealAdapter
        binding.isEmpty = isEmpty
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getZeals(idBovino)
                .subscribeBy(
                        onSuccess = {
                            nextZeal.text = it.second?.toStringFormat() ?: "No hay celos registrados"
                            val zeals = it.first
                            listZealAdapter.zeals = zeals ?: emptyList()
                            listZealAdapter.zealsServed = it.third
                            val empty = zeals?.isEmpty() ?: true
                            isEmpty.set(empty)
                        }
                )

        dis add viewModel.getOnServiceForBovine(idBovino)
                .subscribeBy(
                        onSuccess = {
                            listZealAdapter.onService = it.isNotEmpty()
                        }
                )

        dis add fabAddZeal.clicks()
                .subscribeBy(
                        onNext = {
                            startActivity<AddZealActivity>(ID_BOVINO to idBovino)
                        }
                )

        dis add listZealAdapter.clickAddService.subscribeBy(
                onNext = {
                    startActivity<AddServiceActivity>(ARG_ID to idBovino, ARG_ZEAL to it.toStringFormat())
                }

        )
    }

    companion object {
        const val ID_BOVINO: String = "idBovino"
        const val ARG_ZEAL: String = "celo"
        fun instance(idBovino: String): ListZealFragment = ListZealFragment().apply {
            arguments = Bundle().apply {
                putString(ID_BOVINO, idBovino)
            }

        }
    }
}