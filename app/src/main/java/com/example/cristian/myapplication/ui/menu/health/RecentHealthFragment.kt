package com.example.cristian.myapplication.ui.menu.health


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.ProxStates
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.RecentHealthAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.ui.menu.health.detail.HealthDetailActivity
import com.example.cristian.myapplication.ui.menu.health.detail.HealthDetailActivity.Companion.ID_FIRST_HEALTH
import org.jetbrains.anko.support.v4.startActivity
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_recent_health.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class RecentHealthFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    @Inject
    lateinit var adapterRecent: RecentHealthAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_health, container, false)
    }


    override fun onResume() {
        super.onResume()

        recyclerRecentHealth.adapter = adapterRecent
        recyclerRecentHealth.layoutManager = LinearLayoutManager(activity)

        dis add adapterRecent.clickHealth
                .subscribeBy(
                        onNext = {
                            startActivity<HealthDetailActivity>(HealthDetailActivity.ID_HEALTH to it._id!!, ID_FIRST_HEALTH to it.idDosisUno!!)
                        }
                )

        dis add viewModel.getHealth(idFinca)
                .subscribeBy(
                        onSuccess = {
                            if (it.isEmpty()) emptyHealthText.visibility = View.VISIBLE else emptyHealthText.visibility = View.GONE
                            adapterRecent.health = it
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add fabAddHealthFragment.clicks()
                .subscribe {

                    startActivity<AddHealthActivity>()
                }
    }

    companion object {

        fun instance(): RecentHealthFragment = RecentHealthFragment()
    }
}
