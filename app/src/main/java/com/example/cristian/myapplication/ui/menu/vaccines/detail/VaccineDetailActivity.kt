package com.example.cristian.myapplication.ui.menu.vaccines.detail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.ui.adapters.VaccineDetailPagerAdapter
import com.example.cristian.myapplication.util.fixColor
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
        title = "Detalles Aplicación"
        setSupportActionBar(toolbar)
        tabs.setupWithViewPager(pager)
        pager.adapter = adapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        val clr = fixColor(7)
        tabs.setBackgroundColor(clr)

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    companion object {
        const val ID_VACCINE = "idVacuna"
        const val ID_FIRST_VACCINE = "idDosisUno"
    }
}
