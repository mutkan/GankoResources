package com.example.cristian.myapplication.ui.menu

import android.app.SearchManager
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.MenuAdapter
import com.example.cristian.myapplication.ui.menu.bovine.ListBovineFragment
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.putFragment
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.jakewharton.rxbinding2.widget.queryTextChanges
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_menu.*
import javax.inject.Inject


class MenuActivity : AppCompatActivity(),Injectable,HasSupportFragmentInjector {

    @Inject
    lateinit var injector:DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }

    lateinit var toggle: ActionBarDrawerToggle
    var adapter: MenuAdapter = MenuAdapter()
    val dis: LifeDisposable = LifeDisposable(this)
    @Inject
    lateinit var nav: MenuNavigation
    var phone: Boolean = true
    var state: Int = 1
    lateinit var menu: Menu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        phone = resources.getBoolean(R.bool.phone)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle = ActionBarDrawerToggle(this, drawer, R.string.menu_open, R.string.menu_close)


        recycler.adapter = adapter
        adapter.items = viewModel.data

        val gridManager: GridLayoutManager = GridLayoutManager(this, 2)
        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int = when(viewModel.data[position].type){
                MenuViewModel.MenuItem.TYPE_MENU -> 1
                else -> 2
            }
        }
        recycler.layoutManager = gridManager
        clickOnMenu(viewModel.content, true)
        putFragment(R.id.content_frame,ListBovineFragment.instance())

    }

    override fun onResume() {
        super.onResume()

        dis add adapter.clickMenu
                .subscribe {
                    if(phone) {
                        drawer.closeDrawers()
                        when (it) {
                            1 -> nav.navigateToFarm()
                            in 2..11 -> clickOnMenu(it)
                            12 -> nav.navigateToLogout()
                        }
                    }else{
                        when (it) {
                            1 -> nav.navigateToFarm()
                            in 2..11 -> clickOnMenu(it)
                            12 -> nav.navigateToLogout()
                        }
                    }
                }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search_toolbar).actionView as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))

        dis add searchView.queryTextChanges()
                .doOnNext { viewModel.querySubject.onNext(it.toString()) }
                .subscribe()

        return super.onCreateOptionsMenu(menu)
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        when(item!!.itemId){
            R.id.filter_toolbar -> drawer.openDrawer(GravityCompat.END)
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        val MENU1 = 1
        val MENU2 = 2
    }

    fun showMenu1(){
        state = MENU1
        onPrepareOptionsMenu(menu)
    }
    fun showMenu2(){
        state = MENU2
        onPrepareOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        if (state == MENU1){
            menuInflater.inflate(R.menu.toolbar_menu, menu)
        }else{
            menuInflater.inflate(R.menu.toolbar_search, menu)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    fun clickOnMenu(content: Int, firsttime:Boolean = false){

        viewModel.content = content
        val item = viewModel.data[content]
        val colorID = viewModel.selectedColors[content-2]
        val color = ContextCompat.getColor(this, colorID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = viewModel.getStatusBarColor(color)
        }
        supportActionBar?.setTitle(item.title)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
        adapter.selectItem(content, colorID)

        if(!firsttime) when(content){
            2-> showMenu1()
            in 3..11-> showMenu2()
        }

        when(content){
            2-> nav.navigateToBovines()
            3-> nav.navigateToMilk()
            4-> nav.navigateToFeeding()
            5-> nav.navigateToManage()
            6-> nav.navigateToMovements()
            7-> nav.navigateToVaccination()
            8-> nav.navigateToHealth()
            9-> nav.navigateToStraw()
            10-> nav.navigateToMeadow()
            11-> nav.navigateToReports()
        }

    }

}
