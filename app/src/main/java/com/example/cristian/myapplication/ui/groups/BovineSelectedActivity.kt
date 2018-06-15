package com.example.cristian.myapplication.ui.groups

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.util.fixColor

class BovineSelectedActivity : AppCompatActivity() {

    val selecteds:MutableList<String> by lazy { intent.extras.getStringArray(EXTRA_SELECTED).toMutableList()}
    val color:Int by lazy { intent.extras.getInt(EXTRA_COLOR, 12) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bovine_selected)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.bovine_selected)
        fixColor(color)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_SELECTED = "selected"
        const val EXTRA_COLOR = "color"

    }
}
