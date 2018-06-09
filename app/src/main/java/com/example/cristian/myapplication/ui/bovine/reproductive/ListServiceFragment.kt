package com.example.cristian.myapplication.ui.bovine.reproductive

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentListServiceBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListServiceAdapter
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
    private val dis: LifeDisposable = LifeDisposable(this)
    @Inject
    lateinit var adapter: ListServiceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_service, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler.adapter = adapter

    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getServicesForBovine(idBovino)
                .subscribeBy(
                        onSuccess = {
                            adapter.services = it
                        }
                )

        dis add fabAddService.clicks()
                .subscribeBy(
                        onNext = {
                            startActivity<AddServiceActivity>(ARG_ID to idBovino)
                        }
                )
    }

    companion object {
        const val ARG_ID = "ID_BOVINO"
        fun instance(idBovino: String): ListServiceFragment = ListServiceFragment().apply { arguments = Bundle().apply { putString(ARG_ID, idBovino) } }
    }
}