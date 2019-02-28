package com.ceotic.ganko.ui.menu

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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.ALARM_HEALTH
import com.ceotic.ganko.data.models.ALARM_MANAGE
import com.ceotic.ganko.data.models.ALARM_MEADOW_EXIT
import com.ceotic.ganko.data.models.ALARM_MEADOW_OCUPATION
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.MenuAdapter
import com.ceotic.ganko.ui.common.PageChangeListener
import com.ceotic.ganko.ui.common.SearchBarActivity
import com.ceotic.ganko.ui.menu.notifications.NotificationsFragment
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_menu.*
import org.jetbrains.anko.toast
import javax.inject.Inject


class MenuActivity : SearchBarActivity(MENU_SEARCH_FILTER), Injectable, HasSupportFragmentInjector, NotificationsFragment.OnNotificationSelected {

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
    var contentValue = 0

    var farm:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        intent.extras?.getString("farm")?.run {
            farm = this
            viewModel.setFarmId(this)
        }


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
            when (intent.extras.getInt("fragment", -1)) {
                0 -> clickOnMenu(9, true)
                1 -> clickOnMenu(6, true)
                2 -> clickOnMenu(8, true)
                4 -> clickOnMenu(11, true)
                13 -> clickOnMenu(13, true)
            }
        } else {
            clickOnMenu(viewModel.content, true)
        }

    }


    override fun onResume() {
        super.onResume()
        if(farm != null){
            dis add viewModel.getFarm(farm!!)
                    .subscribeBy(
                            onSuccess = {
                                toast("Finca $it seleccionada")
                                adapter.notifyDataSetChanged()
                            },
                            onComplete = {}
                    )
        }

        if(!viewModel.validatePlan())
            planMsg.visibility = View.VISIBLE

        dis add adapter.clickMenu
                .subscribe {
                    drawer.closeDrawers()
                    when (it) {
                        1 -> nav.navigateToFarm()
                        in 2..13 -> clickOnMenu(it)
                        14 -> nav.navigateToLogout()
                    }

                }

        dis add FilterFragment.filter
                .subscribe { drawer.closeDrawers() }

        dis add PageChangeListener.tabChanges
                .subscribe {
                    if (it != 0) setClearMenu()
                    else setSearchMenu(contentValue)
                }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        if (intent.extras != null) {
            when (intent.extras.getInt("fragment", -1)) {
                0 -> setMenuForContent(9)
                1 -> setMenuForContent(6)
                2 -> setMenuForContent(8)
                4 -> setMenuForContent(11)
            }
        }
        return true
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

        if (!firsttime) setMenuForContent(content)

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

    fun setMenuForContent(content: Int) {
        contentValue = content
        when (content) {
            2 -> setSearchFilterMenu(content)
            in 3..6 -> setSearchMenu(content)
            7 -> setClearMenu()
            in 8..10 -> setSearchMenu(content)
            11, 12, 13 -> setClearMenu()
        }
    }

    override fun onNotificationSelected(alarm: Int) {
        when(alarm){
            ALARM_MANAGE -> clickOnMenu(6)
            ALARM_HEALTH -> clickOnMenu(9)
            in ALARM_MEADOW_OCUPATION .. ALARM_MEADOW_EXIT -> clickOnMenu(11)
            else -> clickOnMenu(8)
        }

    }

}