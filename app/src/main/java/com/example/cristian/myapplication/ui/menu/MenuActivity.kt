package com.example.cristian.myapplication.ui.menu

import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.adapters.MenuAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

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

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle = ActionBarDrawerToggle(this, drawer, R.string.menu_open, R.string.menu_close)


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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
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


}
