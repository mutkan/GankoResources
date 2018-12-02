package com.ceotic.ganko.ui.groups

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.MenuItem
import com.ceotic.ganko.R
import com.ceotic.ganko.ui.common.SearchBarActivity
import com.ceotic.ganko.ui.menu.FilterFragment
import com.ceotic.ganko.util.fixColor
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_add_group.*
import javax.inject.Inject


class AddGroupActivity : SearchBarActivity(MENU_SEARCH_FILTER), HasSupportFragmentInjector {

    @Inject
    lateinit var injector:DispatchingAndroidInjector<Fragment>
    lateinit var filterDis:Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.add_group_title)

        fixColor(12)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SelectBovineFragment.instance(true))
                .commit()

    }

    override fun onResume() {
        super.onResume()
        filterDis = FilterFragment.filter
                .subscribe { drawer.closeDrawers() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            finish()
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            android.R.id.home -> finish()
            R.id.filter_toolbar -> if(!drawer.isDrawerOpen(Gravity.END))drawer.openDrawer(Gravity.END)
            else drawer.closeDrawers()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        filterDis.dispose()
    }


}
