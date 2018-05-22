package com.example.cristian.myapplication.ui.feed

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.ActivitySelectFeedBinding
import com.example.cristian.myapplication.ui.adapters.SelectFeedAdapter
import kotlinx.android.synthetic.main.activity_select_feed.*

class SelectFeedActivity : AppCompatActivity() {

    lateinit var binding:ActivitySelectFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_select_feed)
        setupViewPager(SelectFeedPager)
        tabsFeedSelect.setupWithViewPager(SelectFeedPager)
    }

    private fun setupViewPager(selectFeedPager: ViewPager?) {
        val adapter = SelectFeedAdapter(supportFragmentManager)

        val f1 = SelectFeedFragment.instance()
        adapter.addFragment(f1, "Bovinos")

        val f2 = GroupsFeedFragment.instance()
        adapter.addFragment(f2, "Grupos")

        selectFeedPager?.adapter = adapter
    }
}
