package com.ceotic.ganko.ui.menu.movement


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Pradera
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.MovementUsedAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
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
    val clickRemoveGroupFromMeadow: PublishSubject<Pradera> = PublishSubject.create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meadow_used, container, false)
    }

    override fun onResume() {
        super.onResume()
        getMeadows()
        usedMeadowsList.adapter = adapter
        usedMeadowsList.layoutManager = LinearLayoutManager(context)

        dis add adapter.onClickMeadow
                .subscribe {
                    removeGroupFromMeadow(it)
                }

        dis add clickRemoveGroupFromMeadow
                .flatMapSingle { x ->
                    viewmodel.freeMeadow("${x.identificador}")
                            .map { x }
                }
                .flatMapSingle { pradera ->
                    val grupo = pradera.group
                    pradera.apply {
                        fechaSalida = Date()
                        available = true
                        bovinos = listOf()
                        group = null
                    }
                    viewmodel.updateMeadow(pradera._id!!, pradera)
                            .flatMapMaybe { viewmodel.getGroupById(grupo!!) }
                            .flatMapSingle {
                                viewmodel.updateGroup(it.apply { this.pradera = null })
                            }
                }

                .subscribeBy(
                        onNext = {
                            toast("Datos guardados correctamente")
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

    }


    fun removeGroupFromMeadow(pradera: Pradera) {
        alert {
            title = "¿Desea desocupar esta pradera?"
            yesButton {
                clickRemoveGroupFromMeadow.onNext(pradera)
            }
            noButton { }
        }.show()
    }

    fun getMeadows() {
        dis add viewmodel.getUsedMeadows(viewmodel.getFarmId())
                .subscribeBy {
                    noData.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                    adapter.data = it

                }
    }

    companion object {
        const val MEADOWS = "meadows"
        fun instance(): MeadowUsedFragment = MeadowUsedFragment()
    }


}
