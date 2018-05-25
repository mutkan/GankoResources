package com.example.cristian.myapplication.ui.feed
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentSelectFeedBinding
import com.example.cristian.myapplication.ui.adapters.ListFeedSelectBovinesAdapter
import com.example.cristian.myapplication.util.Data
import kotlinx.android.synthetic.main.fragment_select_feed.*

class SelectFeedFragment : Fragment() /*, Injectable*/ {

    lateinit var binding: FragmentSelectFeedBinding
    val adapter:ListFeedSelectBovinesAdapter = ListFeedSelectBovinesAdapter()
    companion object {
        fun instance(): SelectFeedFragment= SelectFeedFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_feed, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkBox.setOnClickListener {
            if (checkBox.isChecked){ adapter.selectAll(1)}
            else adapter.selectAll(2)
        }
        selectFeedBovines.adapter = adapter
        selectFeedBovines.layoutManager = LinearLayoutManager(activity)
        adapter.feedSelectbovines = Data.bovines
    }



}
