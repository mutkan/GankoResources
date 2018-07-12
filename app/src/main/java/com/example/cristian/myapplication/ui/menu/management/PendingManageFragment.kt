package com.example.cristian.myapplication.ui.menu.management


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.RegistroManejo
import com.example.cristian.myapplication.databinding.FragmentPendingManageBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.PendingManageAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.util.*
import javax.inject.Inject


class PendingManageFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentPendingManageBinding
    @Inject
    lateinit var adapter: PendingManageAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    val skiped: PublishSubject<RegistroManejo> = PublishSubject.create()
    val from: Date = Date()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending_manage, container, false)
        binding.isEmpty = isEmpty
        binding.recyclerPendingManage.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dis add adapter.clickApplyManage
                .subscribeBy(
                        onNext = {
                            startActivity<AddManageActivity>("edit" to true, AddManageActivity.PREVIOUS_MANAGE to it)
                        }
                )

        dis add adapter.clickSkipManage
                .subscribeBy(
                        onNext = {
                            showAlert(it)
                        }
                )

        dis add viewModel.getPendingManages(from)
                .subscribeBy(
                        onNext = {
                            adapter.pendingManages = it
                            isEmpty.set(it.isEmpty())
                        }
                )

        dis add skiped
                .flatMapSingle {
                    val manejo = it.apply { estadoProximo = RegistroManejo.SKIPED }
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
        fun instance(): PendingManageFragment = PendingManageFragment()
    }


}
