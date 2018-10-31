package com.ceotic.ganko.ui.menu.management.detail

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.ui.adapters.ManageDetailPagerAdapter
import com.ceotic.ganko.util.fixColor
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_manage_detail.*
import javax.inject.Inject

class ManageDetailActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    val idManage: String by lazy { intent.getStringExtra(ID_MANAGE) }
    val idDosisUno: String by lazy { intent.getStringExtra(ID_FIRST_MANAGE) }
    @Inject
    lateinit var adapter: ManageDetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_detail)
        setSupportActionBar(toolbar)
        val clr = fixColor(5)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        tabs.setupWithViewPager(pager)
        pager.adapter = adapter
        tabs.setBackgroundColor(clr)
        supportActionBar?.title = "Detalles Aplicacion"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    companion object {
        const val ID_MANAGE = "idVacuna"
        const val ID_FIRST_MANAGE = "idAplicacionUno"
    }

}
