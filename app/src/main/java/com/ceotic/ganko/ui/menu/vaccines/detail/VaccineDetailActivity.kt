package com.ceotic.ganko.ui.menu.vaccines.detail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.ceotic.ganko.R
import com.ceotic.ganko.ui.adapters.VaccineDetailPagerAdapter
import com.ceotic.ganko.util.fixColor
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_vaccine_detail.*
import javax.inject.Inject

class VaccineDetailActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    val idVaccine:String by lazy { intent.getStringExtra(ID_VACCINE) }
    val idDosisUno: String by lazy { intent.getStringExtra(ID_FIRST_VACCINE) }
    @Inject
    lateinit var adapter: VaccineDetailPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vaccine_detail)
        setSupportActionBar(toolbar)
        val clr = fixColor(7)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        tabs.setupWithViewPager(pager)
        pager.adapter = adapter
        tabs.setBackgroundColor(clr)
        supportActionBar?.title = "Detalles Aplicación"

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    companion object {
        const val ID_VACCINE = "idVacuna"
        const val ID_FIRST_VACCINE = "idAplicacionUno"
    }
}
