package com.example.cristian.myapplication.ui.menu.health


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cristian.myapplication.BR.isEmpty
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.databinding.FragmentNextHealthBinding
import com.example.cristian.myapplication.databinding.TemplateSkipHealthBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.NextHealthAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.add
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_next_health.*
import kotlinx.android.synthetic.main.fragment_recent_health.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
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
            private val mAlert: AlertDialog by lazy { AlertDialog.Builder(activity!!.applicationContext).create() }
    val from: Date = Date()
    val to: Date by lazy { from.add(Calendar.DATE, 7)!! }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

       binding = DataBindingUtil.inflate(inflater,R.layout.fragment_next_health,container,false)
        binding.recyclerNextHealth.adapter= adapterNext
        binding.recyclerNextHealth.layoutManager = LinearLayoutManager(activity)
        return binding.root
    }



    override fun onResume() {
        super.onResume()

        dis add viewModel.getNextHealth1(from, to)
                .subscribeBy(
                        onNext = {
                            if(it.isEmpty()) emptyNextHealthText.visibility = View.VISIBLE else emptyNextHealthText.visibility = View.GONE
                            adapterNext.health = it

                            Log.d("MANEJOS", it.toString())
                        })


        dis add adapterNext.clickApply
                .subscribeBy( onNext = {
                    var san = it
                    san.proximaAplicacion = 1
                    alert( "Confirme si desea aplicar sanidad"){yesButton {aplicar(san) }
                        noButton { toast("Cancelado") }
                    }.show()},
                        onError = {
                            toast(it.message!!)
                        })


        dis add adapterNext.clickSkip
                .subscribeBy( onNext = {
                    var san = it
                    san.proximaAplicacion = 2
                    alert( "Confirme si desea omitir sanidad"){yesButton {update(san) }
                        noButton { toast("Cancelado") }
                    }.show()})

    }

    private fun update(san:Sanidad) {
    dis add  viewModel.updateHealth(san).subscribeBy(
                onSuccess = {toast("Omitido correctamente")
                    refresh()
                }
    )
    }


    private fun aplicar(san:Sanidad) {
        dis add  viewModel.updateHealth(san).subscribeBy(
                onSuccess = {toast("Aplicado correctamente")
                }
        )}

    fun refresh(){
        dis add viewModel.getPendingHealrh().subscribeBy (
            onSuccess = {
                PendingHealthFragment.instance().adapter.pending = it
                }
            )
    }

    companion object {
        fun instance(): NextHealthFragment = NextHealthFragment()
    }
}
