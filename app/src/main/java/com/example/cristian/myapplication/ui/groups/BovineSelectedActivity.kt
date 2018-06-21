package com.example.cristian.myapplication.ui.groups

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.transition.TransitionManager
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.couchbase.lite.internal.support.Log
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.groups.adapters.SelectedAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.fixColor
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.toObservable
import kotlinx.android.synthetic.main.activity_bovine_selected.*
import org.jetbrains.anko.contentView
import javax.inject.Inject

class BovineSelectedActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: GroupViewModel by lazy { buildViewModel<GroupViewModel>(factory) }
    @Inject
    lateinit var adapter: SelectedAdapter

    val dis: LifeDisposable = LifeDisposable(this)

    val selecteds: MutableList<String> by lazy { intent.extras.getStringArray(EXTRA_SELECTED).toMutableList() }
    val color: Int by lazy { intent.extras.getInt(EXTRA_COLOR, 12) }
    val editable:Boolean by lazy { intent.extras.getBoolean(EXTRA_EDITABLE, true) }

    var data: List<Bovino> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bovine_selected)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.bovine_selected)
        fixColor(color)
    }

    override fun onResume() {
        super.onResume()
        adapter.editable = editable
        list.adapter = adapter

        dis add viewModel.listSelected(selecteds)
                .subscribe { rta ->
                    data = rta
                    adapter.data = rta.toMutableList()
                }

        dis add adapter.onRemove
                .subscribe {
                    TransitionManager.beginDelayedTransition(contentView!! as ViewGroup)
                    btnGroup.visibility = View.VISIBLE
                    adapter.data.removeAt(it)
                    adapter.notifyDataSetChanged()
                }

        dis add btnCancel.clicks()
                .subscribe {
                    TransitionManager.beginDelayedTransition(contentView!! as ViewGroup)
                    btnGroup.visibility = View.GONE
                    adapter.data = data.toMutableList()
                }

        dis add btnOk.clicks()
                .map { adapter.data.map { it._id!! }.toTypedArray() }
                .subscribe { rta->
                    val data = Intent()
                    data.putExtra(DATA_ITEMS, rta)
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_SELECTED = "selected"
        const val EXTRA_COLOR = "color"
        const val EXTRA_EDITABLE = "editable"
        const val DATA_ITEMS = "items"

    }
}
