package com.example.cristian.myapplication.ui.feed


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.adapters.ListGroupsAdapter
import com.example.cristian.myapplication.util.Data
import kotlinx.android.synthetic.main.fragment_groups_feed.*
import java.util.*


class GroupsFeedFragment : Fragment() /*,Injectable*/ {

    val random:Random = Random()
    /*
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel : FeedViewModel by lazy { buildViewModel<FeedViewModel>(factory) }

    @Inject
    lateinit var adapter: ListGroupsAdapter
    val dis: LifeDisposable = LifeDisposable(this)



    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    private val idFinca: String by lazy { viewModel.getFarmId() }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_feed, container, false)
    }


    override fun onResume() {
        super.onResume()
        recyclerListGroups.adapter= adapter
        dis add viewModel.getAllGroups(idFinca)
                .subscribeBy(
                        onNext = {
                            isEmpty.set(it.isEmpty())
                            adapter.groups = it
                            android.util.Log.d("GRUPOS", it.toString())
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )
        //Data pruebas
        adapter.groups = grupos
    }

    companion object {
        fun instance(): GroupsFeedFragment = GroupsFeedFragment()
    }
*/
    val adapter: ListGroupsAdapter = ListGroupsAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_feed, container, false)
    }

    override fun onResume() {
        super.onResume()
        recyclerListGroups.adapter= adapter
        recyclerListGroups.layoutManager = LinearLayoutManager(activity)
        //Data pruebas
        for(group in Data.grupos)
        {
            var red = random.nextInt(255)
            var green = random.nextInt(255)
            var blue = random.nextInt(255)

            var color = Color.rgb(red, green, blue)
            group.color = color
        }
        adapter.groups = Data.grupos
    }

    companion object {
        fun instance(): GroupsFeedFragment = GroupsFeedFragment()
    }

}
