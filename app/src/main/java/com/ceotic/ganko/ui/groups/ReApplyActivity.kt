package com.ceotic.ganko.ui.groups

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.groups.adapters.SelectAdapter
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import kotlinx.android.synthetic.main.fragment_select_bovine.*
import javax.inject.Inject

class ReApplyActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: GroupViewModel by lazy { buildViewModel<GroupViewModel>(factory) }

    private val selecteds: HashMap<String, Boolean> = HashMap()
    val adapter: SelectAdapter = SelectAdapter()

    val dis: LifeDisposable = LifeDisposable(this)

    val id: String by lazy { intent.extras.getString(EXTRA_ID) }
    private val ids: MutableList<String> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_select_bovine)

        title = getString(R.string.select_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        adapter.selecteds = selecteds
        list.adapter = adapter
        dis add viewModel.listBovinesByDocId(id)
                .flatMapObservable { it.toObservable() }
                .doOnNext {
                    selecteds[it._id!!] = true
                    ids.add(it._id!!)
                }
                .toList()
                .subscribe { bovines ->
                    adapter.data = bovines
                    number.text = "${bovines.size}"
                    showBar()
                }
    }

    override fun onResume() {
        super.onResume()

        dis add btnNext.clicks()
                .flatMapSingle {
                    ids.toObservable().filter { !selecteds.containsKey(it) }
                            .toList()
                }
                .subscribe { noBovines ->
                    val keys = selecteds.keys.toTypedArray()
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(DATA_BOVINES, keys)
                        putExtra(DATA_NO_BOVINES, noBovines.toTypedArray())
                    })
                    finish()
                }

        dis add btnClear.clicks()
                .subscribe {
                    selecteds.clear()
                    adapter.notifyDataSetChanged()
                    number.text = "0"
                    hideBar()
                }

        dis add adapter.onSelectBovine
                .subscribe {
                    val size = selecteds.size
                    number.text = "$size"
                    if (size == 1) showBar()
                    else if (size == 0) hideBar()
                }
    }

    private fun hideBar() {
        TransitionManager.beginDelayedTransition(bottomBar)
        bottomBar.visibility = View.GONE
        bottomBar.translationY = resources.getDimension(R.dimen.select_bar)
    }

    private fun showBar() {
        TransitionManager.beginDelayedTransition(bottomBar)
        bottomBar.visibility = View.VISIBLE
        bottomBar.translationY = 0f
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_re_applay, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) finish()
        else selectAll()
        return super.onOptionsItemSelected(item)
    }


    private fun selectAll() {
        dis add ids.toObservable()
                .doOnNext { selecteds[it] = true }
                .subscribeBy(onComplete = {
                    adapter.notifyDataSetChanged()
                    number.text = "${selecteds.size}"
                    if (bottomBar.visibility == View.GONE) showBar()
                })
    }

    companion object {
        const val REQUEST_CODE = 1452
        val EXTRA_ID = "extra_id"

        val DATA_BOVINES = "data.bovines"
        val DATA_NO_BOVINES = "data.no.bovines"

    }
}
