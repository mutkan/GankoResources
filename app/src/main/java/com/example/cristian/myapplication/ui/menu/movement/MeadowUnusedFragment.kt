package com.example.cristian.myapplication.ui.menu.movement


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.data.models.Movimiento
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.databinding.TemplateSpinnerGroupBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.MovementUnusedAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_meadow_unused.*
import org.jetbrains.anko.customView
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MeadowUnusedFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val adapter = MovementUnusedAdapter()
    val dis = LifeDisposable(this)
    lateinit var arrayGroup: ArrayList<Group>
    val clickAddGroup: PublishSubject<Pair<Pradera, Group>> = PublishSubject.create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meadow_unused, container, false)
    }

    override fun onResume() {
        super.onResume()
        getUnusedMeadows()
        unusedMeadowsList.adapter = adapter
        unusedMeadowsList.layoutManager = LinearLayoutManager(context)

        dis add adapter.onClickMeadow
                .subscribe {
                    findGroupsToUseMeadow(it)
                }

        dis add viewmodel.getGroups(viewmodel.getFarmId())
                .subscribeBy(
                        onNext = {
                            arrayGroup = ArrayList(it)
                        }
                )

        dis add clickAddGroup.flatMapSingle {
            val pradera = it.first
            val grupo = it.second
            val mov = Movimiento(null,null,null,pradera.identificador!!.toString(),grupo.bovines,Date(),viewmodel.getFarmId())
            pradera.apply {
                fechaOcupacion = Date()
                available = false
                group = grupo.nombre
                bovinos = grupo.bovines
            }
            viewmodel.updateMeadow(pradera._id!!, pradera)
                    .flatMap {
                        viewmodel.updateGroup(grupo.apply { this.pradera = pradera.identificador!!.toString() })
                    }
                    .flatMap {
                        viewmodel.insertMovement(mov)
                    }
        }.subscribeBy(
                onNext = {
                    toast("Datos guardados correctamente")
                }
        )

    }

    fun getUnusedMeadows() {
        dis add viewmodel.getUnusedMeadows(viewmodel.getFarmId())
                .subscribeBy(
                        onNext = {
                            noData.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                            adapter.data = it
                        }
                )
    }

    fun findGroupsToUseMeadow(pradera: Pradera) {
        if (arrayGroup.size >0 ){
            alert {
                val viewBind = TemplateSpinnerGroupBinding.inflate(layoutInflater, null, false)
                viewBind.groups = arrayGroup
                title = "Añada un grupo a esta pradera"
                customView {
                    this.addView(viewBind.root, null)
                }
                yesButton {
                    val group = viewBind.selectedGroup.selectedItem as Group
                    clickAddGroup.onNext(pradera to group)
                }
                noButton {}
            }.show()
        }else{
            toast("No hay grupos disponibles")
        }
    }


    companion object {
        const val MEADOWS = "meadows"
        fun instance(): MeadowUnusedFragment = MeadowUnusedFragment()
    }

}
