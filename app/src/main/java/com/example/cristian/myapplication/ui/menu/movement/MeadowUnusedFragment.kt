package com.example.cristian.myapplication.ui.menu.movement


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.databinding.TemplateSpinnerGroupBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.MovementUnusedAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import kotlinx.android.synthetic.main.fragment_meadow_unused.*
import org.jetbrains.anko.customView
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.util.*
import javax.inject.Inject

class MeadowUnusedFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val adapter = MovementUnusedAdapter()
    val dis = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meadow_unused, container, false)
    }

    override fun onResume() {
        super.onResume()
        unusedMeadowsList.adapter = adapter
        unusedMeadowsList.layoutManager = LinearLayoutManager(context)
        dis add viewmodel.getUnusedMeadows(viewmodel.getFarmId())
                .subscribeBy {
                    if (it.isEmpty()) noData.visibility = View.VISIBLE
                    else {
                        adapter.data = it
                        noData.visibility = View.GONE
                    }
                }
        dis add adapter.onClickMeadow
                .subscribe {
                    findGroupsToUseMeadow(it)
                }
    }

    fun findGroupsToUseMeadow(pradera: Pradera) {
        val arrayGroup = arrayListOf<String>()
        dis add viewmodel.getGroups(viewmodel.getFarmId())
                .flatMapObservable {
                    if(it.isEmpty()) toast("No hay grupos disponibles para el movimiento")
                    it.toObservable()
                }
                .map {
                    toast("group")
                    arrayGroup.add(it.nombre)
                }.subscribe {
                    alert {
                        val spinner = layoutInflater.inflate(R.layout.template_spinner_group, null) as Spinner
                        val bind = DataBindingUtil.bind<TemplateSpinnerGroupBinding>(spinner)!!
                        title = "Añada un grupo a esta pradera"
                        customView {

                            bind.groups = arrayGroup
                            this.addView(spinner, null)
                        }
                        yesButton {
                            pradera.fechaOcupacion = Date()
                            pradera.available = false
                            pradera.group = spinner.selectedItem.toString()
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
                        noButton {}
                    }.show()
                }

    }


    companion object {
        const val MEADOWS = "meadows"
        fun instance(): MeadowUnusedFragment = MeadowUnusedFragment()
    }

}
