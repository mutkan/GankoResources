package com.example.cristian.myapplication.ui.groups

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.MenuItem
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.common.SearchBarActivity
import com.example.cristian.myapplication.ui.groups.adapters.SelectPagerAdapter
import com.example.cristian.myapplication.ui.menu.FilterFragment
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.fixColor
import com.jakewharton.rxbinding2.support.v4.view.pageSelections
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_select.*
import javax.inject.Inject

class SelectActivity : SearchBarActivity(MENU_SEARCH_FILTER), HasSupportFragmentInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>

    val color by lazy { intent.extras.getInt(EXTRA_COLOR, 12) }
    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        setSupportActionBar(toolbar)
        val clr = fixColor(color)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tabs.setBackgroundColor(clr)

        pager.adapter = SelectPagerAdapter(supportFragmentManager).apply {
            titles = resources.getStringArray(R.array.select_titles)
        }

        tabs.setupWithViewPager(pager)


        supportActionBar?.title = getString(R.string.select_title)
    }

    override fun onResume() {
        super.onResume()

        dis add FilterFragment.filter
                .subscribe { drawer.closeDrawers() }

        dis add pager.pageSelections()
                .skip(1)
                .subscribe {
                    if (it == 0) setSearchFilterMenu()
                    else setClearMenu()
                }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.filter_toolbar) {
            drawer.openDrawer(Gravity.END)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_CANCELED)
        finish()
        return true
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector


    companion object {
        const val DATA_GROUP = "groups"
        const val DATA_BOVINES = "bovines"
        const val EXTRA_COLOR = "color"

        const val REQUEST_SELECT = 1002
    }

}
