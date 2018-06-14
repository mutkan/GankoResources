package com.example.cristian.myapplication.ui.groups

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.util.fixColor
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject


class AddGroupActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var injector:DispatchingAndroidInjector<Fragment>

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            finish()
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

}
