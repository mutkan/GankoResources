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
open class SearchBarActivity(private var menuType:Int): AppCompatActivity(){

    private var searchView:SearchView? = null
    private var queryDis:Disposable? = null
    private lateinit var searchMenu:Menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        searchMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        setupClearBar()
        when(menuType){
            MENU_SEARCH -> {
                menuInflater.inflate(R.menu.toolbar_menu_3, menu)
                setupSearchBar(menu)
            }
            MENU_SEARCH_FILTER -> {
                menuInflater.inflate(R.menu.toolbar_menu_2, menu)
                setupSearchBar(menu)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setupSearchBar(menu:Menu){
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
    }

    fun setClearMenu(){
        menuType = MENU_CLEAR
        onPrepareOptionsMenu(searchMenu)
    }

    fun setSearchMenu(){
        menuType = MENU_SEARCH
        onPrepareOptionsMenu(searchMenu)
    }

    fun setSearchFilterMenu(){
        menuType = MENU_SEARCH_FILTER
        onPrepareOptionsMenu(searchMenu)
    }



    private fun setupClearBar(){
        searchView?.setOnSearchClickListener(null)
        searchView?.setOnCloseListener(null)
        queryDis?.dispose()
        queryDis = null
    }

    override fun onDestroy() {
        super.onDestroy()
        queryDis?.dispose()
    }

    override fun onBackPressed() {
        if(searchView?.isIconified != false) super.onBackPressed()
        else searchView?.isIconified = true
    }

    companion object {
        const val MENU_CLEAR = 0
        const val MENU_SEARCH = 1
        const val MENU_SEARCH_FILTER = 2
        val query: PublishSubject<String> = PublishSubject.create()
    }

}