package com.example.cristian.myapplication.ui.menu.bovine


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListBovineAdapter
import com.example.cristian.myapplication.ui.adapters.ListFeedBovineAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import kotlinx.android.synthetic.main.fragment_list_bovine.*
import kotlinx.android.synthetic.main.fragment_list_feed.*
import javax.inject.Inject

class ListFeedFragment : Fragment(), Injectable {

    @Inject
    lateinit var adapter: ListFeedBovineAdapter
    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_feed, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerlistFeedBovines.adapter = adapter

    }


}
