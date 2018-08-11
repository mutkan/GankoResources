package com.example.cristian.myapplication.ui.common

import android.annotation.SuppressLint
import android.app.SearchManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import com.example.cristian.myapplication.R
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChanges
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

@SuppressLint("Registered")
open class SearchBarActivity(private val withFilter:Boolean): AppCompatActivity(){

    private var searchView:SearchView? = null
    private lateinit var queryDis:Disposable

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if(withFilter) menuInflater.inflate(R.menu.toolbar_menu_2, menu)
        else menuInflater.inflate(R.menu.toolbar_menu_3, menu)

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.search_toolbar).actionView as SearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.setIconifiedByDefault(true)
        searchView?.setOnSearchClickListener { supportActionBar?.setDisplayHomeAsUpEnabled(false) }
        searchView?.setOnCloseListener {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            false
        }


        queryDis = searchView!!.queryTextChanges()
                .subscribe { query.onNext(it.toString()) }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        queryDis.dispose()
    }

    override fun onBackPressed() {
        if(searchView?.isIconified != false) super.onBackPressed()
        else searchView?.isIconified = true
    }

    companion object {
        val query: PublishSubject<String> = PublishSubject.create()
    }

}