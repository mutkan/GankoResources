package com.example.cristian.myapplication.ui.menu.meadow

import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.text.InputType
import android.text.method.TransformationMethod
import android.view.*
import android.widget.*
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.databinding.TemplateAddMeadowBinding
//import com.example.cristian.myapplication.databinding.FragmentMeadowBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.MeadowAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.text
import com.fasterxml.jackson.core.JsonParser
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import kotlinx.android.synthetic.main.fragment_meadow.*
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*
import org.jetbrains.anko.customView
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.*
import org.jetbrains.anko.yesButton
import java.util.*
import javax.inject.Inject

class MeadowFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }

    val adapter: MeadowAdapter by lazy { MeadowAdapter() }
    val dis: LifeDisposable = LifeDisposable(this)
    var identificador = 0
    lateinit var ipd:ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meadow, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        gridMeadow.adapter = adapter
        val layout = GridLayoutManager(context, 10)
        gridMeadow.layoutManager = layout
        gridMeadow.itemAnimator.changeDuration = 0
    }

    override fun onResume() {
        super.onResume()
        ipd = indeterminateProgressDialog("CARGANDO","")
        ipd.show()

        if(ipd.isShowing && adapter.data.isNotEmpty()) ipd.dismiss()

        dis add viewModel.getMeadows(viewModel.getFarmId())
                .subscribeBy {
                    if (it.first.isEmpty()) createMeadows()
                    else {
                        adapter.data = it.first.toMutableList()
                        identificador = it.second.toInt()
                        ipd.dismiss()
                    }
                }

        dis add adapter.onClickMeadow
                .subscribeBy {
                    val meadow = it
                    if (meadow.isUsedMeadow == false) {
                        alert {
                            val viewbind = TemplateAddMeadowBinding.inflate(layoutInflater,null,false)
                            viewbind.spnMeadowUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                    meadow.tamanoEnHectareas = false
                                }

                                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                    meadow.tamanoEnHectareas = p2 == 1
                                }

                            }
                            customView {
                                this.addView(viewbind.root, null)
                            }
                            yesButton {
                                if (viewbind.meadowSize.text() != "") {
                                    ipd.show()
                                    meadow.isUsedMeadow = true
                                    meadow.isEmptyMeadow = false
                                    meadow.available = true
                                    meadow.fechaSalida = Date()
                                    identificador += 1
                                    meadow.identificador = identificador
                                    meadow.tamano = viewbind.meadowSize.text().toFloat()
                                    viewModel.updateMeadow(meadow._id!!, meadow)
                                            .flatMap {
                                                viewModel.getMeadows(viewModel.getFarmId())
                                            }.subscribeBy {
                                                adapter.data = it.first.toMutableList()
                                                identificador = it.second.toInt()
                                                ipd.dismiss()
                                            }
                                } else toast(R.string.empty_fields)
                            }
                            noButton { }
                        }.show()
                    } else {
                        val options = listOf("Administrar", "Alertas","Remover")
                        selector("Seleccione una opcion", options) { _, i ->
                            when (i) {
                                0 -> startActivity<ManageMeadowActivity>(MEADOWID to meadow._id!!)
                                1 -> startActivity<ManageMeadowAlertActivity>(MEADOWID to meadow._id!!)
                                else -> {
                                    ipd.show()
                                    meadow.isUsedMeadow = false
                                    meadow.isEmptyMeadow = true
                                    meadow.available = null
                                    identificador -= 1
                                    meadow.identificador = null
                                    meadow.tamano = 0f
                                    viewModel.updateMeadow(meadow._id!!, meadow)
                                            .flatMap {
                                                viewModel.getMeadows(viewModel.getFarmId())
                                            }.subscribeBy {
                                                adapter.data = it.first.toMutableList()
                                                identificador = it.second.toInt()
                                                ipd.dismiss()
                                            }
                                }
                            }
                        }
                    }

                }


    }

    fun createMeadows() {
        val pradera = Pradera(idFinca = viewModel.getFarmId(), isUsedMeadow = false, isEmptyMeadow = true)

        dis add (0..99).toObservable()
                .flatMapSingle { viewModel.saveMeadow(pradera) }
                .count()
                .flatMap { viewModel.getMeadows(viewModel.getFarmId()) }
                .subscribeBy {
                    adapter.data = it.first.toMutableList()
                    identificador = it.second.toInt()
                    ipd.dismiss()
                }
    }

    companion object {
        const val MEADOWID = "meadow"
        fun instance(): MeadowFragment = MeadowFragment()
    }
}
