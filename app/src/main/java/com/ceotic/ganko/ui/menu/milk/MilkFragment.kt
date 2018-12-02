package com.ceotic.ganko.ui.menu.milk


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.MilkAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_milk.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


class MilkFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    @Inject
    lateinit var adapter: MilkAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_milk, container, false)
    }

    override fun onResume() {
        super.onResume()

        adapter.notifyDataSetChanged()
        recyclerMilk.adapter = adapter
        recyclerMilk.layoutManager = LinearLayoutManager(activity)

        dis add viewModel.getMilk(idFinca)
                .subscribeBy(
                        onNext = {
                            adapter.milk = it
                            if (it.isEmpty()) emptyListMilk.visibility = View.VISIBLE
                            else emptyListMilk.visibility = View.GONE
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add fabAddMilkFragment.clicks()
                .subscribe {
                    startActivity<AddMilkActivity>()
                }
    }

    companion object {
        fun instance(): MilkFragment = MilkFragment()
    }


}
