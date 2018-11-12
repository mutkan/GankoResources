package com.ceotic.ganko.ui.menu.meadow.size


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Pradera
import com.ceotic.ganko.databinding.FragmentSizeBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.menu.meadow.MeadowViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.text
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_size.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


class SizeFragment : Fragment(),Injectable {

    val meadow: Pradera by lazy { arguments!!.getParcelable<Pradera>(MEADOW) }
    lateinit var binding: FragmentSizeBinding
    val dis = LifeDisposable(this)

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel: MeadowViewModel by lazy { buildViewModel<MeadowViewModel>(factory) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_size, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.meadow = meadow
        if (meadow.tamanoEnHectareas!!) meadowUnitSize.setSelection(1) else meadowUnitSize.setSelection(0)
        when (meadow.tipoGraminea) {
            "Selecciona una" -> gramineaType.setSelection(0)
            "Pasto Castillo" -> gramineaType.setSelection(1)
            "Pasto Barrera" -> gramineaType.setSelection(2)
            "Pasto king grass" -> gramineaType.setSelection(3)
            "Maralfalfa" -> gramineaType.setSelection(4)
            else -> gramineaType.setSelection(0)
        }

        dis add btnCancel.clicks()
                .subscribe { activity!!.finish() }

        dis add btnAdd.clicks()
                .subscribe {
                    if (meadowSize.text() != "") {
                        updateMeadow()
                    } else {
                        toast(R.string.empty_fields)
                    }
                }

    }

    fun updateMeadow() {
        meadow.tamano = meadowSize.text().toFloat()
        meadow.tamanoEnHectareas = meadowUnitSize.selectedItem.toString() == "Hectareas"
        meadow.tipoGraminea = gramineaType.selectedItem.toString()
        dis add viewmodel.updateMeadow(meadow)
                .subscribeBy (
                        onSuccess = {
                            toast("Datos Guardados")

                        },
                        onError = {
                            toast("Error desconocido, intente de nuevo ")
                        }
                )
    }

    companion object {
        const val MEADOW = "meadow"
        fun instance(meadow: Pradera): SizeFragment {
            val fragment = SizeFragment()
            val arguments = Bundle()
            arguments.putParcelable(MEADOW, meadow)
            fragment.arguments = arguments
            return fragment
        }
    }

}
