package com.example.cristian.myapplication.ui.menu.meadow

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.databinding.ActivityManageMeadowBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ManageMeadowAdapter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_manage_meadow.*
import javax.inject.Inject

class ManageMeadowActivity : AppCompatActivity(), Injectable, HasSupportFragmentInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    val meadow: Pradera by lazy { intent.getParcelableExtra<Pradera>(MEADOW) }
    lateinit var binding: ActivityManageMeadowBinding
    val adapter: ManageMeadowAdapter by lazy { ManageMeadowAdapter(this, supportFragmentManager, meadow) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_meadow)
        title = "Administrar pradera "+meadow.identificador
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        manageTab.setupWithViewPager(managePager)
    }

    override fun onResume() {
        super.onResume()
        managePager.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val MEADOW = "meadow"
    }
}
