package com.example.cristian.myapplication.ui.menu.notifications


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.RegistroManejo
import com.example.cristian.myapplication.data.models.RegistroVacuna
import com.example.cristian.myapplication.data.models.Sanidad
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.NotificationsListAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.ui.menu.health.detail.HealthDetailActivity
import com.example.cristian.myapplication.ui.menu.management.detail.ManageDetailActivity
import com.example.cristian.myapplication.ui.menu.vaccines.AddVaccineActivity
import com.example.cristian.myapplication.ui.menu.vaccines.detail.VaccineDetailActivity
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.add
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.startActivity
import java.util.*
import javax.inject.Inject

class NotificationsFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    @Inject
    lateinit var adapter: NotificationsListAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    var isEmpty: Boolean = false
        set(value) {
            field = value
            emptyMsgNotifications.visibility = if (value) View.VISIBLE else View.GONE
        }
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
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        notificationsList.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getNotifications(from, to)
                .subscribeBy(
                        onSuccess = {
                            adapter.data = it
                            isEmpty = it.isEmpty()
                        }
                )

        dis add adapter.onClickNotification
                .subscribeBy(
                        onNext = {
                            when(it.type){
                                RegistroVacuna::class.simpleName -> startActivity<VaccineDetailActivity>(VaccineDetailActivity.ID_VACCINE to it._id!!, VaccineDetailActivity.ID_FIRST_VACCINE to it.idAplicacionUno!!)
                                RegistroManejo::class.simpleName -> startActivity<ManageDetailActivity>(ManageDetailActivity.ID_MANAGE to it._id!!, ManageDetailActivity.ID_FIRST_MANAGE to it.idAplicacionUno!!)
                                Sanidad::class.simpleName -> startActivity<HealthDetailActivity>(HealthDetailActivity.ID_HEALTH to it._id!!, HealthDetailActivity.ID_FIRST_HEALTH to it.idAplicacionUno!!)
                            }

                        }
                )

    }


    companion object {
        fun instance(): NotificationsFragment = NotificationsFragment()
    }


}
