package com.ceotic.ganko.ui.menu.meadow

//import com.example.cristian.myapplication.databinding.FragmentMeadowBinding
import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Pradera
import com.ceotic.ganko.databinding.TemplateAddMeadowBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.MeadowAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.text
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import kotlinx.android.synthetic.main.fragment_meadow.*
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
    lateinit var ipd: ProgressDialog

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
        ipd = indeterminateProgressDialog("CARGANDO", "")
        ipd.show()

        if (ipd.isShowing && adapter.data.isNotEmpty()) ipd.dismiss()

        dis add viewModel.getMeadows(viewModel.getFarmId())
                .subscribeBy {
                    if (it.first.isEmpty()) createMeadows()
                    else {
                        adapter.data = it.first.toMutableList()
                        ipd.dismiss()
                    }
                }

        dis add adapter.onClickMeadow
                .subscribeBy {
                    val meadow = it
                    if (meadow.usedMeadow == false) {
                        alert {
                            val viewbind = TemplateAddMeadowBinding.inflate(layoutInflater, null, false)
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
                                    meadow.usedMeadow = true
                                    meadow.emptyMeadow = false
                                    meadow.available = true
                                    meadow.fechaSalida = Date()
                                    meadow.tamano = viewbind.meadowSize.text().toFloat()
                                    dis add calculateIndentificator()
                                            .flatMap {
                                                meadow.identificador = it
                                                viewModel.updateMeadow(meadow._id!!, meadow)
                                            }
                                            .flatMap {
                                                viewModel.getMeadows(viewModel.getFarmId())
                                            }.subscribeBy {
                                                adapter.data = it.first.toMutableList()
                                                ipd.dismiss()
                                            }
                                } else toast(R.string.empty_fields)
                            }
                            noButton { }
                        }.show()
                    } else {
                        val options = listOf("Administrar", "Alertas", "Remover")
                        selector("Seleccione una opcion", options) { _, i ->
                            when (i) {
                                0 -> startActivity<ManageMeadowActivity>(MEADOWID to meadow._id!!)
                                1 -> startActivity<ManageMeadowAlertActivity>(MEADOWID to meadow._id!!)
                                else -> {
                                    ipd.show()
                                    meadow.usedMeadow = false
                                    meadow.emptyMeadow = true
                                    meadow.available = null
                                    meadow.identificador = null
                                    meadow.tamano = 0f
                                    dis add viewModel.updateMeadow(meadow._id!!, meadow)
                                            .flatMap {
                                                viewModel.getMeadows(viewModel.getFarmId())
                                            }.subscribeBy {
                                                adapter.data = it.first.toMutableList()
                                                ipd.dismiss()
                                            }
                                }
                            }
                        }
                    }

                }


    }

    fun createMeadows() {
        val listPraderas = mutableListOf<Pradera>()
        for (i in 0..99) {
            val pradera = Pradera(idFinca = viewModel.getFarmId(), usedMeadow = false, emptyMeadow = true, orderValue = i)
            listPraderas.add(pradera)
        }
        dis add listPraderas.toObservable()
                .flatMapSingle {
                    viewModel.saveMeadow(it)
                }
                .count()
                .flatMap { viewModel.getMeadows(viewModel.getFarmId()) }
                .subscribeBy {
                    adapter.data = it.first.toMutableList()
                    ipd.dismiss()
                }
    }

    fun calculateIndentificator(): Single<Int> {
        var prev = 0
        return adapter.data.toObservable()
                .filter { it.identificador != null }
                .map { it.identificador!! }
                .toList()
                .map {
                    it.sort()
                    it
                }
                .flatMap { ids->
                    ids.toObservable().filter {
                        val valid = prev != (it - 1)
                        if (!valid) prev = it
                        valid
                    }
                            .map { prev + 1 }
                            .first(0)
                            .map { if(it == 0) (ids.lastOrNull() ?: 0) + 1 else it }
                }
                .applySchedulers()
    }


    companion object {
        const val MEADOWID = "meadow"
        fun instance(): MeadowFragment = MeadowFragment()
    }
}
