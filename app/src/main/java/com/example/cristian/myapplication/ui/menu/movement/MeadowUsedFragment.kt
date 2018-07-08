package com.example.cristian.myapplication.ui.menu.movement


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
import com.example.cristian.myapplication.ui.adapters.MovementUsedAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_meadow_used.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.util.*
import javax.inject.Inject


class MeadowUsedFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val adapter = MovementUsedAdapter()
    val dis = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meadow_used, container, false)
    }

    override fun onResume() {
        super.onResume()
        usedMeadowsList.adapter = adapter
        usedMeadowsList.layoutManager = LinearLayoutManager(context)
        dis add viewmodel.getUsedMeadows(viewmodel.getFarmId())
                .subscribeBy {
                    if (it.isEmpty()) noData.visibility = View.VISIBLE
                    else {
                        adapter.data = it
                        noData.visibility = View.GONE
                    }
                }
        dis add adapter.onClickMeadow
                .subscribe {
                    removeGroupFromMeadow(it)
                }
    }

    fun removeGroupFromMeadow(pradera: Pradera) {
        alert {
            title = "¿Desea desocupar esta pradera?"
            yesButton {
                pradera.fechaSalida = Date()
                pradera.available = true
                dis add viewmodel.updateMeadow(pradera._id!!, pradera)
                        .subscribeBy(
                                onSuccess = {
                                    toast("Datos guardados correctamente")
                                },
                                onError = {
                                    toast(it.message!!)
                                }
                        )
            }
            noButton { }
        }
    }

    companion object {
        const val MEADOWS = "meadows"
        fun instance(): MeadowUsedFragment = MeadowUsedFragment()
    }


}