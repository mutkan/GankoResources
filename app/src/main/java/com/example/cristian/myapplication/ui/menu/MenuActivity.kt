package com.example.cristian.myapplication.ui.menu

import android.arch.lifecycle.ViewModelProvider
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.MenuAdapter
import com.example.cristian.myapplication.ui.common.PageChangeListener
import com.example.cristian.myapplication.ui.common.SearchBarActivity
import com.example.cristian.myapplication.ui.menu.health.HealthFragment
import com.example.cristian.myapplication.ui.menu.management.ManageFragment
import com.example.cristian.myapplication.ui.menu.vaccines.VaccinesFragment
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.putFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_menu.*
import javax.inject.Inject


class MenuActivity : SearchBarActivity(MENU_SEARCH_FILTER), Injectable, HasSupportFragmentInjector {

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

        val gridManager = GridLayoutManager(this, 2)
        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = when (viewModel.data[position].type) {
                MenuViewModel.MenuItem.TYPE_MENU -> 1
                else -> 2
            }
        }
        recycler.layoutManager = gridManager


        if (intent.extras != null) {
            when (intent.extras.get("fragment")) {
                0 -> {
                    putFragment(R.id.content_frame, HealthFragment.instance())
                }
                1 -> putFragment(R.id.content_frame, ManageFragment.instance())
                else -> putFragment(R.id.content_frame, VaccinesFragment.instance())
            }
        } else {
            clickOnMenu(viewModel.content, true)
//            putFragment(R.id.content_frame, ListBovineFragment.instance())
        }

    }


    override fun onResume() {
        super.onResume()

        if (intent.extras != null) {
            when (intent.extras.get("fragment")) {
                0 -> clickOnMenu(9)
                1 -> clickOnMenu(6)
                2 -> clickOnMenu(8)
                else -> clickOnMenu(11)
            }
        } else {
            clickOnMenu(viewModel.content, true)
//            putFragment(R.id.content_frame, ListBovineFragment.instance())
        }

        dis add adapter.clickMenu
                .subscribe {
                    if (phone) drawer.closeDrawers()
                    when (it) {
                        1 -> nav.navigateToFarm()
                        in 2..13 -> clickOnMenu(it)
                        14 -> nav.navigateToLogout()
                    }

                }

        dis add FilterFragment.filter
                .subscribe { drawer.closeDrawers() }

        dis add PageChangeListener.tabChanges
                .subscribe{
                    if (it != 0) setClearMenu()
                    else setSearchMenu()
                }


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
            R.id.filter_toolbar ->
                if (!drawer.isDrawerOpen(GravityCompat.END)) drawer.openDrawer(GravityCompat.END)
                else drawer.closeDrawers()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val SELECTED_FRAGMENT = "selectedFragment"
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
            2 -> setSearchFilterMenu()
            in 3..6 -> setSearchMenu()
            7 -> setClearMenu()
            in 8..10 -> setSearchMenu()
            11, 12, 13 -> setClearMenu()
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