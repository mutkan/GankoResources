package com.example.cristian.myapplication.ui.menu.meadow

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
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meadow, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        gridMeadow.adapter = adapter
        val layout = GridLayoutManager(context, 10)
        gridMeadow.layoutManager = layout
        gridMeadow.itemAnimator.changeDuration = 0

        dis add viewModel.getMeadows(viewModel.getFarmId())
                .subscribeBy {
                    if (it.first.isEmpty()) createMeadows()
                    else {
                        adapter.data = it.first.toMutableList()
                        identificador = it.second.toInt()
                    }
                }

        dis add adapter.onClickMeadow
                .subscribeBy {
                    val meadow = it
                    if (meadow.isUsedMeadow == false) {
                        alert {
                            val title = TextView(context)
                            title.text = "Ingrese el tamaño de la pradera"
                            title.gravity = Gravity.CENTER
                            title.setPadding(0, 20, 0, 0)
                            title.textSize = 20f
                            customTitle = title
                            val et = EditText(context)
                            et.inputType = InputType.TYPE_CLASS_NUMBER
                            et.width = 200
                            val spinner = Spinner(context)
                            val spinnerArray = ArrayList<String>()
                            spinnerArray.add("m²")
                            spinnerArray.add("Hectareas")
                            spinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
                            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                    meadow.tamanoEnHectareas = false
                                }

                                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                    meadow.tamanoEnHectareas = p2 == 1
                                }
                            }

                            val linearLayout = LinearLayout(context)
                            linearLayout.orientation = LinearLayout.HORIZONTAL
                            linearLayout.addView(et)
                            linearLayout.gravity = Gravity.CENTER
                            linearLayout.addView(spinner)
                            customView {
                                this.addView(linearLayout, null)
                            }
                            yesButton {
                                if (et.text() != "") {
                                    meadow.isUsedMeadow = true
                                    meadow.isEmptyMeadow = false
                                    meadow.available = true
                                    meadow.fechaSalida = Date()
                                    identificador += 1
                                    meadow.identificador = identificador
                                    meadow.tamano = et.text().toFloat()
                                    viewModel.updateMeadow(meadow._id!!, meadow)
                                            .flatMap {
                                                viewModel.getMeadows(viewModel.getFarmId())
                                            }.subscribeBy {
                                                adapter.data = it.first.toMutableList()
                                                identificador = it.second.toInt()
                                            }
                                } else toast(R.string.empty_fields)
                            }
                            noButton { }
                        }.show()
                    } else {
                        val options = listOf("Administrar", "Remover")
                        selector("Seleccione una opcion", options, { dialogInterface, i ->
                            when (i) {
                                0 -> startActivity<ManageMeadowActivity>(MEADOWID to meadow._id!!)
                                else -> {
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
                                            }
                                }
                            }
                        })
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
                }
    }

    companion object {
        const val MEADOWID = "meadow"
        fun instance(): MeadowFragment = MeadowFragment()
    }
}
