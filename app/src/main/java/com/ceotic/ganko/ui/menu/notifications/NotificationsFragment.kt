package com.ceotic.ganko.ui.menu.notifications


import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.NotificationsListAdapter
import com.ceotic.ganko.ui.bovine.DetailBovineActivity
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.ui.menu.health.detail.HealthDetailActivity
import com.ceotic.ganko.ui.menu.management.detail.ManageDetailActivity
import com.ceotic.ganko.ui.menu.vaccines.detail.VaccineDetailActivity
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.add
import com.ceotic.ganko.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_notifications.*
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
    val from: Date by lazy { Date(cal.timeInMillis).add(Calendar.DATE,-7)!! }
    val to: Date by lazy { from.add(Calendar.DATE, 10)!! }
    lateinit var notificationSelected:OnNotificationSelected

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        notificationSelected = context as OnNotificationSelected
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onResume() {
        super.onResume()
        adapter.icons = activity!!.resources.getIntArray(R.array.notification_icon)
        notificationsList.adapter = adapter
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
                            when(it.alarma!!){
                                ALARM_18_MONTHS -> startActivity<DetailBovineActivity>(DetailBovineActivity.EXTRA_ID to it.reference!!)
                                in ALARM_2_MONTHS .. ALARM_MANAGE -> notificationSelected.onNotificationSelected(it.alarma)
                                in ALARM_SECADO .. ALARM_ZEAL_84 -> startActivity<DetailBovineActivity>(DetailBovineActivity.EXTRA_ID to it.reference!!)
                                ALARM_REJECT_DIAGNOSIS_3 -> startActivity<DetailBovineActivity>(DetailBovineActivity.EXTRA_ID to it.reference!!)
                                in ALARM_MEADOW_OCUPATION .. ALARM_MEADOW_EXIT -> notificationSelected.onNotificationSelected(it.alarma)
                            }

                        }
                )

    }


    companion object {
        fun instance(): NotificationsFragment = NotificationsFragment()
    }


    interface OnNotificationSelected{
        fun onNotificationSelected(alarm:Int)
    }

}
