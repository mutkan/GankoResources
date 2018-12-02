package com.ceotic.ganko.ui.bovine.reproductive

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.ActivityReproductiveBovineBinding
import com.ceotic.ganko.ui.adapters.ReproductiveBovineAdapter
import com.ceotic.ganko.ui.bovine.reproductive.ListZealFragment.Companion.ID_BOVINO
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class ReproductiveBvnActivity : AppCompatActivity(),HasSupportFragmentInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var adapter:ReproductiveBovineAdapter
    val idBovino:String by lazy { intent.getStringExtra(ID_BOVINO) ?: "" }
    lateinit var binding:ActivityReproductiveBovineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reproductive_bovine)
        binding.toolbar.title = "Reproductivo"
        binding.pager.adapter = adapter
        setSupportActionBar(binding.toolbar)
        binding.tabs.setupWithViewPager(binding.pager)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}
