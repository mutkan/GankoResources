package com.example.cristian.myapplication.ui

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.View
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.adapters.MenuAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity(), DrawerLayout.DrawerListener {

    lateinit var toggle: ActionBarDrawerToggle
    var adapter: MenuAdapter = MenuAdapter()
    val dis: LifeDisposable = LifeDisposable(this)
    var nav: MenuNavigation = MenuNavigation()
    var phone: Boolean = true
    lateinit var menuViewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        menuViewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        phone = resources.getBoolean(R.bool.phone)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle = ActionBarDrawerToggle(this, drawer, R.string.menu_open, R.string.menu_close)
        drawer.addDrawerListener(this)

        recycler.adapter = adapter
        adapter.items = menuViewModel.data

        val gridManager: GridLayoutManager = GridLayoutManager(this, 2)
        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int = when(menuViewModel.data[position].type){
                MenuViewModel.MenuItem.TYPE_MENU -> 1
                else -> 2
            }
        }
        recycler.layoutManager = gridManager
        clickOnMenu(menuViewModel.content)

    }

    override fun onResume() {
        super.onResume()

        fun onCreateOptionsMenu(menu: Menu): Boolean{
            menuInflater.inflate(R.menu.toolbar_menu, menu)
            return true
        }

        dis add adapter.clickMenu
                .subscribe {
                    if(phone) {
                        drawer.closeDrawers()
                        when (it) {
                            1 -> nav.navigateToFarm()
                            in 2..9 -> clickOnMenu(it)
                            10 -> nav.navigateToLogout()
                        }
                    }else{
                        when (it) {
                            1 -> nav.navigateToFarm()
                            in 2..9 -> clickOnMenu(it)
                            10 -> nav.navigateToLogout()
                        }
                    }
                }
    }


    fun clickOnMenu(content: Int){

        menuViewModel.content = content
        val item = menuViewModel.data[content]
        val colorID = menuViewModel.selectedColors[content-2]
        val color = ContextCompat.getColor(this, colorID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = menuViewModel.getStatusBarColor(color)
        }
        supportActionBar?.setTitle(item.title)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
        adapter.selectItem(content, colorID)

        when(content){
            2-> nav.navigateToBovines()
            3-> nav.navigateToFeeding()
            4-> nav.navigateToManage()
            5-> nav.navigateToMovements()
            6-> nav.navigateToVaccination()
            7-> nav.navigateToHealth()
            8-> nav.navigateToPrairies()
            9-> nav.navigateToReports()
        }

    }

    override fun onDrawerStateChanged(newState: Int) {
        toggle.onDrawerStateChanged(newState)
    }

    override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
        toggle.onDrawerSlide(drawerView, slideOffset)
    }

    override fun onDrawerClosed(drawerView: View?) {
        toggle.onDrawerClosed(drawerView)
    }

    override fun onDrawerOpened(drawerView: View?) {
        toggle.onDrawerOpened(drawerView)
    }

}
