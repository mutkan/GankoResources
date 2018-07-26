package com.example.cristian.myapplication.ui.bovine.reproductive

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
import com.example.cristian.myapplication.databinding.FragmentListZealBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListZealAdapter
import com.example.cristian.myapplication.ui.bovine.reproductive.ListServiceFragment.Companion.ARG_ID
import com.example.cristian.myapplication.ui.bovine.reproductive.add.AddServiceActivity
import com.example.cristian.myapplication.ui.bovine.reproductive.add.AddZealActivity
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.toStringFormat
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_list_zeal.*
import org.jetbrains.anko.support.v4.startActivity
import java.util.*
import java.util.concurrent.TimeUnit
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
    var daysSinceLastZeal: Long = 10
        set(value) {
            field = value
            fabAddZeal.visibility = if (onService || value < 10) View.GONE else View.VISIBLE
        }
    var onService = false
        set(value) {
            field = value
            fabAddZeal.visibility = if (value || daysSinceLastZeal < 10) View.GONE else View.VISIBLE
        }

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
                            listZealAdapter.zeals = it.first ?: emptyList()
                            val empty = it.first?.isEmpty() ?: true
                            isEmpty.set(empty)
                            val lastZeal = it.first?.first()
                            verifyLastZeal(lastZeal)
                        }
                )

        dis add viewModel.getOnServiceForBovine(idBovino)
                .subscribeBy(
                        onSuccess = {
                            val activeService = it.isNotEmpty()
                            listZealAdapter.activeService = activeService
                            onService = activeService
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

    private fun verifyLastZeal(lastZeal: Date?) {
        if (lastZeal != null) {
            val now = Date()
            val dif = now.time - lastZeal.time
            daysSinceLastZeal = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)

        }
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