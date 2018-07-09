package com.example.cristian.myapplication.ui.menu.health


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.BR.isEmpty
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.NextHealthAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.add
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_next_health.*
import kotlinx.android.synthetic.main.fragment_recent_health.*
import org.jetbrains.anko.support.v4.toast
import java.util.*
import javax.inject.Inject


class NextHealthFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }
    val from: Date = Date()
    val to: Date by lazy { from.add(Calendar.DATE, 7)!! }

    @Inject
    lateinit var adapterNext: NextHealthAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_next_health, container, false)
    }

    override fun onResume() {
        super.onResume()

        recyclerNextHealth.adapter = adapterNext
        recyclerNextHealth.layoutManager = LinearLayoutManager(activity)


        /*dis add viewModel.getNextHealth(idFinca, 0)
                .subscribeBy(
                onSuccess = {
                    if(it.isEmpty()) emptyNextHealthText.visibility = View.VISIBLE else emptyNextHealthText.visibility = View.GONE
                    adapterNext.health = it
                },
                onError = {
                    toast(it.message!!)
                }
        )*/

        dis add viewModel.getNextHealth1(from, to)
                .subscribeBy(
                        onNext = {
                            if(it.isEmpty()) emptyNextHealthText.visibility = View.VISIBLE else emptyNextHealthText.visibility = View.GONE
                            adapterNext.health = it
                        }
                )

        dis add adapterNext.clickApply
                .flatMapSingle {  sanidad ->
                    viewModel.updateHealth(sanidad.apply { proximaAplicacion = 1})
                }.subscribe()


        dis add adapterNext.clickSkip
                .flatMapSingle {  sanidad ->
                    viewModel.updateHealth(sanidad.apply { proximaAplicacion = 2})
                }.subscribe()
    }

    companion object {
        fun instance(): NextHealthFragment = NextHealthFragment()
    }
}
