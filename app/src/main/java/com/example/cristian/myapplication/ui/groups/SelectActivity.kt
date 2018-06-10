package com.example.cristian.myapplication.ui.groups

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.groups.adapters.SelectPagerAdapter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_select.*
import javax.inject.Inject

class SelectActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var injector:DispatchingAndroidInjector<Fragment>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pager.adapter = SelectPagerAdapter(supportFragmentManager).apply {
            titles = resources.getStringArray(R.array.select_titles)
        }

        tabs.setupWithViewPager(pager)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    companion object {
        const val DATA_GROUP = "groups"
    }

}
