package com.example.cristian.myapplication.ui.menu.meadow.aforo


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.AforoAdapter
import com.example.cristian.myapplication.ui.menu.meadow.MeadowViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_aforo.*
import kotlinx.android.synthetic.main.fragment_size.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject

class AforoFragment : Fragment(),Injectable {

    val meadow: Pradera by lazy { arguments!!.getParcelable<Pradera>(MEADOW) }
    val adapter: AforoAdapter by lazy { AforoAdapter() }
    val dis = LifeDisposable(this)
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel : MeadowViewModel by lazy { buildViewModel<MeadowViewModel>(factory) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_aforo, container, false)
    }

    override fun onResume() {
        super.onResume()
        listAforo.adapter = adapter
        listAforo.layoutManager = LinearLayoutManager(context)
        if(meadow.aforo!!.isEmpty()) emptyListAforo.visibility = View.VISIBLE
        else {
            adapter.data = meadow.aforo!!
            emptyListAforo.visibility = View.GONE
        }

        dis add viewmodel.getMeadow(meadow._id!!)
                .subscribeBy {
                    adapter.data = meadow.aforo!!
                    if(meadow.aforo!!.isEmpty()) emptyListAforo.visibility = View.VISIBLE
                    else emptyListAforo.visibility = View.GONE
                }

        dis add addAforo.clicks()
                .subscribe { startActivity<AddAforoActivity>(MEADOW to meadow) }
    }

    companion object {
        const val MEADOW = "meadow"
        fun instance(meadow:Pradera):AforoFragment{
            val fragment = AforoFragment()
            val arguments = Bundle()
            arguments.putParcelable(MEADOW, meadow)
            fragment.arguments = arguments
            return fragment
        }
    }

}
