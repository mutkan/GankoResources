package com.example.cristian.myapplication.ui.menu.health


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.HealthAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.ui.menu.straw.StrawFragment
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_health.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class HealthFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    @Inject
    lateinit var adapter: HealthAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health, container, false)
    }

    override fun onResume() {
        super.onResume()

        recyclerHealth.adapter = adapter
        recyclerHealth.layoutManager = LinearLayoutManager(activity)

        dis add viewModel.getHealth(idFinca)
                .subscribeBy(
                        onSuccess = {
                            adapter.health = it
                            if (it.isEmpty()) toast(R.string.empty_list)
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add fabAddHealthFragment.clicks()
                .subscribe {

                }
    }

    companion object {
        fun instance(): HealthFragment = HealthFragment()
    }
}
