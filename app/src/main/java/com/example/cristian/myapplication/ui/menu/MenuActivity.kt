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
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.MenuAdapter
import com.example.cristian.myapplication.ui.menu.bovine.ListBovineFragment
import com.example.cristian.myapplication.ui.menu.health.HealthFragment
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
import com.example.cristian.myapplication.ui.menu.meadow.MeadowFragment
import com.example.cristian.myapplication.ui.menu.vaccines.VaccinesFragment
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.fixColor
import com.example.cristian.myapplication.util.putFragment
import com.jakewharton.rxbinding2.widget.queryTextChanges
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_menu.*
import javax.inject.Inject


class MenuActivity : AppCompatActivity(), Injectable, HasSupportFragmentInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>

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

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END)

        val gridManager: GridLayoutManager = GridLayoutManager(this, 2)
        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = when (viewModel.data[position].type) {
                MenuViewModel.MenuItem.TYPE_MENU -> 1
                else -> 2
            }
        }
        recycler.layoutManager = gridManager


    }


    override fun onResume() {
        super.onResume()

        dis add adapter.clickMenu
                .subscribe {
                    if (phone) {
                        drawer.closeDrawers()
                        when (it) {
                            1 -> nav.navigateToFarm()
                            in 2..13 -> clickOnMenu(it)
                            14 -> nav.navigateToLogout()
                        }
                    } else {
                        when (it) {
                            1 -> nav.navigateToFarm()
                            in 2..13 -> clickOnMenu(it)
                            14 -> nav.navigateToLogout()
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

        if (intent.extras != null) {
            Log.d("pending", "pendiente")
            when (intent.extras.get("fragment")) {
                0 -> clickOnMenu(9)
                1 -> clickOnMenu(6)
                2 -> clickOnMenu(8)
                else -> clickOnMenu(11)
            }
        } else {
            clickOnMenu(viewModel.content, true)
            putFragment(R.id.content_frame, ListBovineFragment.instance())
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        when (item!!.itemId) {
            R.id.filter_toolbar -> drawer.openDrawer(GravityCompat.END)
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        val MENU1 = 1
        val MENU2 = 2
        val NOMENU = 3

    }

    fun noMenu() {
        state = NOMENU
        onPrepareOptionsMenu(menu)
    }

    fun showMenu1() {
        state = MENU1
        onPrepareOptionsMenu(menu)
    }

    fun showMenu2() {
        state = MENU2
        onPrepareOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        if (state == MENU1) {
            menuInflater.inflate(R.menu.toolbar_menu, menu)
        } else if (state == MENU2) {
            menuInflater.inflate(R.menu.toolbar_search, menu)
        } else {
            menu?.clear()
        }

        return super.onPrepareOptionsMenu(menu)
    }

    fun clickOnMenu(content: Int, firsttime: Boolean = false) {

        viewModel.content = content
        val item = viewModel.data[content]
        val colorID = viewModel.selectedColors[content - 2]
        val color = ContextCompat.getColor(this, colorID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = viewModel.getStatusBarColor(color)
        }
        supportActionBar?.setTitle(item.title)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
        adapter.selectItem(content, colorID)

        if (!firsttime) when (content) {
            2 -> showMenu1()
            in 3..6 -> showMenu2()
            7 -> noMenu()
            in 8..10 -> showMenu2()
            11 -> noMenu()
            12, 13 -> showMenu2()
        }

        when (content) {
            2 -> nav.navigateToBovines()
            3 -> nav.navigateToGroups()
            4 -> nav.navigateToMilk()
            5 -> nav.navigateToFeeding()
            6 -> nav.navigateToManage()
            7 -> nav.navigateToMovements()
            8 -> nav.navigateToVaccination()
            9 -> nav.navigateToHealth()
            10 -> nav.navigateToStraw()
            11 -> nav.navigateToMeadow()
            12 -> nav.navigateToReports()
            13 -> nav.navigateToNotification()
        }

    }

}