package com.example.cristian.myapplication.ui.group

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
import javax.inject.Inject

class SelectActivity : AppCompatActivity(), HasSupportFragmentInjector {

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

        val f1 = SelectFragment.instance()
        adapter.addFragment(f1, "Bovinos")

        val f2 = GroupsFragment.instance()
        adapter.addFragment(f2, "Grupos")

        selectFeedPager?.adapter = adapter
    }
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector
}