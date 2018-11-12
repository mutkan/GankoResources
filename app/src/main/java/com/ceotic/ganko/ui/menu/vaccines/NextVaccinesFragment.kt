package com.ceotic.ganko.ui.menu.vaccines


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.RegistroVacuna
import com.ceotic.ganko.data.models.ProxStates.Companion.SKIPED
import com.ceotic.ganko.databinding.FragmentNextVaccinesBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.VaccineAdapter
import com.ceotic.ganko.ui.adapters.VaccineAdapter.Companion.TYPE_NEXT_VACCINATIONS
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.ui.menu.vaccines.AddVaccineActivity.Companion.PREVIOUS_VACCINE
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.add
import com.ceotic.ganko.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.util.*
import javax.inject.Inject

class NextVaccinesFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentNextVaccinesBinding
    @Inject
    lateinit var adapter: VaccineAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val type: Int by lazy { arguments!!.getInt(FRAGMENT_TYPE) }
    val dis: LifeDisposable = LifeDisposable(this)
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    val cal: Calendar  by lazy {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }
    }
    val skiped: PublishSubject<RegistroVacuna> = PublishSubject.create()
    val from: Date by lazy { Date(cal.timeInMillis) }
    val to: Date by lazy { from.add(Calendar.DATE, 7)!! }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_next_vaccines, container, false)
        adapter.tipo = TYPE_NEXT_VACCINATIONS
        binding.isEmpty = isEmpty
        binding.listNextVaccines.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        Log.d("TYPE", type.toString())
        dis add getVaccines()
                .subscribeBy(
                        onNext = {
                            adapter.data = it
                            isEmpty.set(it.isEmpty())
                        }
                )



        dis add adapter.clickRevaccination
                .subscribeBy(onNext = {
                    startActivity<AddVaccineActivity>("edit" to true, PREVIOUS_VACCINE to it)
                })

        dis add adapter.clickSkipVaccine
                .subscribeBy(
                        onNext = {
                            showAlert(it)
                        }
                )

        dis add skiped
                .flatMapSingle {
                    val vacuna = it.apply { estadoProximo = SKIPED }
                    viewModel.updateVaccine(vacuna)
                }.subscribeBy(
                        onNext = {
                            toast("Vacuna Omitida")
                        }
                )
    }

    private fun setRevaccination(registroVacuna: RegistroVacuna): RegistroVacuna {
        val frec = registroVacuna.frecuencia
        val unidadFrec = registroVacuna.unidadFrecuencia
        val fechaProx = when (unidadFrec) {
            "Horas" -> Date().add(Calendar.HOUR, frec)
            "Días" -> Date().add(Calendar.DATE, frec)
            "Meses" -> Date().add(Calendar.MONTH, frec)
            else -> Date().add(Calendar.YEAR, frec)
        }
        return registroVacuna.copy(fecha = Date(), fechaProxima = fechaProx)
    }

    private fun showAlert(registroVacuna: RegistroVacuna) {
        alert {
            title = "Confirmar Omitir Vacuna"
            message = "¿Está seguro de omitir esta aplicación? Esta acción no podrá ser deshecha."
            yesButton {
                message = "Aceptar"
                skiped.onNext(registroVacuna)
            }
            noButton {
                message = "Cancelar"
            }
        }.show()
    }

    private fun getVaccines() = when (type) {
        TYPE_NEXT -> viewModel.getNextVaccines(from, to)
        else -> viewModel.getPendingVaccines(from)
    }


    companion object {
        private const val FRAGMENT_TYPE = "type"
        const val TYPE_NEXT = 0
        const val TYPE_PENDING = 1

        fun instance(type: Int) = NextVaccinesFragment().apply {
            arguments = Bundle().apply {
                putInt(FRAGMENT_TYPE, type)
            }
        }

    }


}
