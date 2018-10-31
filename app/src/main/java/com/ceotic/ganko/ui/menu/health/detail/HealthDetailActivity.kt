package com.ceotic.ganko.ui.menu.health.detail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.ceotic.ganko.R
import com.ceotic.ganko.ui.adapters.HealthDetailPagerAdapter
import com.ceotic.ganko.util.fixColor
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_health_detail.*
import javax.inject.Inject

class HealthDetailActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    val idHealth:String by lazy { intent.getStringExtra(ID_HEALTH) }
    val idAplicacionUno:String by lazy { intent.getStringExtra(ID_FIRST_HEALTH) }
    @Inject
    lateinit var adapter: HealthDetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_detail)
        title ="Detalles Aplicacion"
        setSupportActionBar(toolbar)
        tabs.setupWithViewPager(pager)
        pager.adapter = adapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        val clr = fixColor(8)
        tabs.setBackgroundColor(clr)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector


    companion object {
        const val ID_HEALTH = "idHealth"
        const val ID_FIRST_HEALTH = "idAplicacionUno"
    }
}
