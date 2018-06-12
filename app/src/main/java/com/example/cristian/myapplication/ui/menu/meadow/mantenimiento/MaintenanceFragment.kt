package com.example.cristian.myapplication.ui.menu.meadow.mantenimiento


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.MaintenanceAdapter
import com.example.cristian.myapplication.ui.menu.meadow.MeadowViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_maintenance.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject


class MaintenanceFragment : Fragment(),Injectable {

    val meadow: Pradera by lazy { arguments!!.getParcelable<Pradera>(MEADOW) }
    val adapter:MaintenanceAdapter by lazy { MaintenanceAdapter() }
    val dis = LifeDisposable(this)
    @Inject
    lateinit var factory:ViewModelProvider.Factory
    val viewmodel : MeadowViewModel by lazy { buildViewModel<MeadowViewModel>(factory) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maintenance, container, false)
    }

    override fun onResume() {
        super.onResume()
        listMaintenance.adapter = adapter
        listMaintenance.layoutManager = LinearLayoutManager(context)
        if(meadow.mantenimiento!!.size == 0) emptyListMaintenance.visibility = View.VISIBLE
        else {
            emptyListMaintenance.visibility = View.GONE
            adapter.data = meadow.mantenimiento!!
        }

        dis add viewmodel.getMeadow(meadow._id!!)
                .subscribeBy {
                    emptyListMaintenance.visibility = View.GONE
                    adapter.data = it.mantenimiento!!
                }

        dis add addMaintenance.clicks()
                .subscribe { startActivity<AddMantenimientoActivity>(MEADOW to meadow) }

    }
    companion object {
        const val MEADOW = "meadow"
        fun instance(meadow:Pradera):MaintenanceFragment {
            val fragment = MaintenanceFragment()
            val arguments = Bundle()
            arguments.putParcelable(MEADOW, meadow)
            fragment.arguments = arguments
            return fragment
        }
    }


}
