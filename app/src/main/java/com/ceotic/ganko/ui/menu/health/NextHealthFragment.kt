package com.ceotic.ganko.ui.menu.health


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.ProxStates
import com.ceotic.ganko.databinding.FragmentNextHealthBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.NextHealthAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.ui.menu.health.AddHealthActivity.Companion.PREVIOUS_HEALTH
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.add
import com.ceotic.ganko.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_next_health.*
import org.jetbrains.anko.support.v4.startActivity
import java.util.*
import javax.inject.Inject


class NextHealthFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding : FragmentNextHealthBinding
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }

    @Inject
    lateinit var adapterNext: NextHealthAdapter
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }
    val from: Date = Date()
    val to: Date by lazy { from.add(Calendar.DATE, 7)!! }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_next_health,container,false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerNextHealth.adapter= adapterNext
        binding.recyclerNextHealth.layoutManager = LinearLayoutManager(activity)
        dis add viewModel.getNextHealth(from, to)
                .subscribeBy(
                        onNext = {
                            if(it.isEmpty()) emptyNextHealthText.visibility = View.VISIBLE else emptyNextHealthText.visibility = View.GONE
                            adapterNext.health = it
                            Log.d("SANIDAD", it.toString())
                        }
                )

        dis add adapterNext.clickApply
                .subscribeBy( onNext = {
                    it.estadoProximo = ProxStates.APPLIED
                    startActivity<AddHealthActivity>("edit" to true, PREVIOUS_HEALTH to it)
                })


        dis add adapterNext.clickSkip
                .flatMapSingle {
                    it.estadoProximo = ProxStates.SKIPED
                    viewModel.updateHealth(it)
                }
                .subscribe()

    }

    companion object {
        fun instance(): NextHealthFragment = NextHealthFragment()
    }
}