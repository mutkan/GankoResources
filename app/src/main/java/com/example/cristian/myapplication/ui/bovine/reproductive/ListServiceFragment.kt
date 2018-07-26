package com.example.cristian.myapplication.ui.bovine.reproductive

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentListServiceBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListServiceBovineAdapter
import com.example.cristian.myapplication.ui.bovine.reproductive.add.AddBirthActivity
import com.example.cristian.myapplication.ui.bovine.reproductive.add.AddDiagnosisActivity
import com.example.cristian.myapplication.ui.bovine.reproductive.add.AddServiceActivity
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_list_service.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject

class ListServiceFragment : Fragment(), Injectable {

    lateinit var binding: FragmentListServiceBinding
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ReproductiveBvnViewModel by lazy { buildViewModel<ReproductiveBvnViewModel>(factory) }
    private val idBovino: String by lazy { arguments!!.getString(ARG_ID, "") }
    private val tipo:Int by lazy { arguments!!.getInt(FRAGMENT_TYPE, TYPE_SERVICES) }
    private val dis: LifeDisposable = LifeDisposable(this)
    private val isEmpty:ObservableBoolean = ObservableBoolean(false)
    @Inject
    lateinit var adapter: ListServiceBovineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_service, container, false)
        binding.isEmpty = isEmpty
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler.adapter = adapter

    }

    override fun onResume() {
        super.onResume()

        if (tipo == TYPE_ON_SERVICE){
            dis add viewModel.getOnServiceForBovine(idBovino)
                    .subscribeBy(
                            onSuccess = {
                                adapter.services = it
                                isEmpty.set(it.isEmpty())

                            }
                    )

            dis add adapter.clickAddDiagnostico
                    .subscribeBy(
                            onNext = {
                                val servicio = it.first!!
                                val position = it.second!!
                                startActivity<AddDiagnosisActivity>(ARG_TYPE to TYPE_DIAGNOSIS, ARG_ID to idBovino, ARG_SERVICE to servicio, ARG_POSITION to position)
                            }
                    )
            dis add adapter.clickAddNovedad
                    .subscribeBy(
                            onNext = {
                                val servicio = it.first!!
                                val position = it.second!!
                                startActivity<AddDiagnosisActivity>(ARG_TYPE to TYPE_NOVELTY, ARG_ID to idBovino, ARG_SERVICE to servicio, ARG_POSITION to position)
                            }
                    )
            dis add adapter.clickAddParto
                    .subscribeBy(
                            onNext = {
                                val servicio = it.first!!
                                val position = it.second!!
                                startActivity<AddBirthActivity>(ARG_ID to idBovino, ARG_SERVICE to servicio, ARG_POSITION to position)
                            }
                    )
        }else{
            dis add viewModel.getServicesHistoryForBovine(idBovino)
                    .subscribeBy(
                            onSuccess = {
                                adapter.services = it
                                isEmpty.set(it.isEmpty())
                            }
                    )
        }

    }

    companion object {
        const val ARG_ID = "ID_BOVINO"
        const val ARG_SERVICE = "SERVICIO"
        const val ARG_POSITION = "POSICION"
        const val ARG_TYPE = "TIPO"
        const val TYPE_DIAGNOSIS = 0
        const val TYPE_NOVELTY = 1
        const val FRAGMENT_TYPE = "fragmentType"
        const val TYPE_ON_SERVICE = 1
        const val TYPE_SERVICES = 0
        fun instance(idBovino: String, fragmentType: Int): ListServiceFragment = ListServiceFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID, idBovino)
                putInt(FRAGMENT_TYPE, fragmentType)
            }
        }
    }
}