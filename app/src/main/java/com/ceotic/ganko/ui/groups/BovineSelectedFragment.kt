package com.ceotic.ganko.ui.groups


import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.groups.adapters.BovineSelectedAdapter
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import kotlinx.android.synthetic.main.fragment_bovine_selected.*
import javax.inject.Inject

class BovineSelectedFragment : Fragment(), Injectable {

    val id:String by lazy { arguments!!.getString(ARG_ID) }

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: GroupViewModel by lazy { buildViewModel<GroupViewModel>(factory) }

    @Inject
    lateinit var adapter:BovineSelectedAdapter

    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bovine_selected, container, false)
    }

    override fun onResume() {
        super.onResume()
        list.adapter = adapter

        dis add viewModel.listAllBovinesByDocId(id)
                .subscribe { bovines->
                    adapter.selecteds = bovines.first
                    adapter.noSelecteds = bovines.second
                    adapter.notifyDataSetChanged()
                }
    }


    companion object {
        private val ARG_ID = "id"
        fun instance(id:String) = BovineSelectedFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ID, id)
            }
        }
    }


}
