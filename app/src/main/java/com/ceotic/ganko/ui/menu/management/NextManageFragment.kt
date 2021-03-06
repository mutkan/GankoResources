package com.ceotic.ganko.ui.menu.management


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.RegistroManejo
import com.ceotic.ganko.data.models.RegistroManejo.Companion.SKIPED
import com.ceotic.ganko.databinding.FragmentNextManageBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.NextManageAdapter
import com.ceotic.ganko.ui.menu.management.AddManageActivity.Companion.PREVIOUS_MANAGE
import com.ceotic.ganko.ui.menu.MenuViewModel
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


class NextManageFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentNextManageBinding
    @Inject
    lateinit var adapter: NextManageAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    val from: Date = Date()
    val to: Date by lazy { from.add(Calendar.DATE, 7)!! }
    val skiped: PublishSubject<RegistroManejo> = PublishSubject.create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_next_manage, container, false)
        binding.isEmpty = isEmpty
        binding.recyclerNextManageFragment.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dis add adapter.clickApplyManage
                .subscribeBy (
                        onNext = {
                            startActivity<AddManageActivity>("edit" to true, PREVIOUS_MANAGE to it)
                        }
                )

        dis add adapter.clickSkipManage
                .subscribeBy (
                        onNext = {
                            showAlert(it)
                        }
                )

        dis add viewModel.getNextManages(from, to)
                .subscribeBy(
                        onNext = {
                            adapter.nextManages = it
                            isEmpty.set(it.isEmpty())
                        }
                )

        dis add skiped
                .flatMapSingle {
                    val manejo = it.apply { estadoProximo = SKIPED }
                    viewModel.updateManage(manejo)
                }.subscribeBy(
                        onNext = {
                            toast("Manejo Omitido")
                        }
                )


    }

    private fun showAlert(registroManejo: RegistroManejo) {
        alert {
            title = "Omitir manejo"
            message = "¿Está seguro de omitir esta aplicación? Esta acción no podrá ser revertida."
            yesButton {
                message = "Aceptar"
                skiped.onNext(registroManejo)
            }
            noButton {
                message = "Cancelar"
            }
        }.show()
    }

    companion object {
        fun instance(): NextManageFragment = NextManageFragment()
    }
}
