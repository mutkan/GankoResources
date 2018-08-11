package com.example.cristian.myapplication.ui.menu.milk


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.MilkAdapter
import com.example.cristian.myapplication.ui.bovine.milk.AddMilkBvnActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_recent_health.*
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
