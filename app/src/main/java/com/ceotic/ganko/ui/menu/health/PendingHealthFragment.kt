package com.ceotic.ganko.ui.menu.health


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.ProxStates
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.PendingHealthAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_pending_health.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.util.*
import javax.inject.Inject


class PendingHealthFragment : Fragment() , Injectable {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }
    var attatch:Boolean= false
    var adapter = PendingHealthAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        return  inflater.inflate(R.layout.fragment_pending_health,container,false)
    }



    override fun onResume() {
        super.onResume()
        recyclerPendingHealth.adapter = adapter
        recyclerPendingHealth.layoutManager = LinearLayoutManager(activity)

        dis add viewModel.getPendingHealth(Date())
                .subscribeBy (
                        onNext = {
                            if(it.isEmpty()) emptyPendingHealthText.visibility = View.VISIBLE else emptyPendingHealthText.visibility = View.GONE
                            adapter.pending = it
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add adapter.clickApply
                .subscribeBy( onNext = {
                    it.estadoProximo = ProxStates.APPLIED
                    startActivity<AddHealthActivity>("edit" to true, AddHealthActivity.PREVIOUS_HEALTH to it)
                })


        dis add adapter.clickSkip
                .flatMapSingle {
                    it.estadoProximo = ProxStates.SKIPED
                    viewModel.updateHealth(it)
                }
                .subscribe()
    }




    companion object {
        fun instance() :PendingHealthFragment = PendingHealthFragment()
    }
}
