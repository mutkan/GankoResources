package com.example.cristian.myapplication.ui.feed

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.adapters.SelectFeedAdapter
import kotlinx.android.synthetic.main.activity_select_feed.*

class SelectFeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_feed)

        setupViewPager(SelectFeedPager)
        tabsFeedSelect.setupWithViewPager(SelectFeedPager)
    }

    private fun setupViewPager(selectFeedPager: ViewPager?) {
        val adapter = SelectFeedAdapter(supportFragmentManager)

        val f1 = SelectFeedFragment.newInstance()
        adapter.addFragment(f1, "Bovinos")

        val f2 = GroupsFeedFragment.newInstance()
        adapter.addFragment(f2, "Grupos")

        selectFeedPager?.adapter = adapter
    }
}
