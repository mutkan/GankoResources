package com.example.cristian.myapplication.ui.menu.vaccines


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.databinding.FragmentNextVaccinesBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.VaccineAdapter
import com.example.cristian.myapplication.ui.adapters.VaccineAdapter.Companion.TYPE_NEXT_VACCINATIONS
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.add
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import org.jetbrains.anko.support.v4.startActivity
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
//                .flatMapSingle { vacuna ->
//                    val revacunacion = setRevaccination(vacuna)
//                    viewModel.inserVaccine(revacunacion).flatMap {
//                        viewModel.updateVaccine(vacuna.apply { proxAplicado = true })
//                    } }
                .subscribeBy(
                        onNext = { reg ->
                            startActivity<AddVaccineActivity>("edit" to true, "bovinos" to reg.bovinos!!)
                        }
                )
    }

    private fun setRevaccination(registroVacuna: RegistroVacuna): RegistroVacuna {
        val frecM = registroVacuna.frecuenciaMeses
        return registroVacuna.copy(fecha = Date(), fechaProx = Date().add(Calendar.MONTH, frecM))
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
