package com.example.cristian.myapplication.ui.groups

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.InputType
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.common.SearchBarActivity
import com.example.cristian.myapplication.ui.menu.FilterFragment
import com.example.cristian.myapplication.util.fixColor
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChanges
import com.jakewharton.rxbinding2.view.clicks
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_add_group.*
import org.jetbrains.anko.toast
import javax.inject.Inject


class AddGroupActivity : SearchBarActivity(true), HasSupportFragmentInjector {

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
