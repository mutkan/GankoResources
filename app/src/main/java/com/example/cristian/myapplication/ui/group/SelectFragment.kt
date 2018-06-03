package com.example.cristian.myapplication.ui.group
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.SelectBovinesAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_select_feed.*
import javax.inject.Inject

class SelectFragment : Fragment() , Injectable {

    val adapter: SelectBovinesAdapter = SelectBovinesAdapter()
    val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var factory:ViewModelProvider.Factory



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_feed, container, false)
    }

    override fun onResume() {
        super.onResume()

        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(activity)

        dis add checkBox.clicks()
                .subscribe()

        dis add  adapter.onSelectBovine
                .subscribe()


    }

    companion object {
        fun instance(): SelectFragment = SelectFragment()
    }


}
