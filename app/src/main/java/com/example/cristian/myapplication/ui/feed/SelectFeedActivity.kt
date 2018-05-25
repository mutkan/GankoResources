package com.example.cristian.myapplication.ui.feed

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.ActivitySelectFeedBinding
import com.example.cristian.myapplication.ui.adapters.SelectFeedAdapter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_select_feed.*
import javax.inject.Inject

class SelectFeedActivity : AppCompatActivity(), HasSupportFragmentInjector {

    lateinit var binding: ActivitySelectFeedBinding
    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_feed)
        setupViewPager(binding.selectFeedPager)
        setSupportActionBar(binding.toolbar)
        binding.tabsFeedSelect.setupWithViewPager(binding.selectFeedPager)
    }

    private fun setupViewPager(selectFeedPager: ViewPager?) {
        val adapter = SelectFeedAdapter(supportFragmentManager)

        val f1 = SelectFeedFragment.instance()
        adapter.addFragment(f1, "Bovinos")

        val f2 = GroupsFeedFragment.instance()
        adapter.addFragment(f2, "Grupos")

        selectFeedPager?.adapter = adapter
    }
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector
}