package com.example.cristian.myapplication.ui.menu.health


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.PendingHealthAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_pending_health.*
import kotlinx.android.synthetic.main.fragment_recent_health.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


class PendingHealthFragment : Fragment() , Injectable {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    @Inject
    lateinit var adapter: PendingHealthAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
      return  inflater.inflate(R.layout.fragment_pending_health,container,false)
    }

    override fun onResume() {
        super.onResume()
        recyclerPendingHealth.adapter = adapter
        recyclerPendingHealth.layoutManager = LinearLayoutManager(activity)

        dis add viewModel.getPendingHealrh()
                .subscribeBy (
                        onSuccess = {
                            if(it.isEmpty()) emptyPendingHealthText.visibility = View.VISIBLE else emptyPendingHealthText.visibility = View.GONE
                            adapter.pending = it
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )}




    companion object {
        fun instance() :PendingHealthFragment = PendingHealthFragment()
    }
}
