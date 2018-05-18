package com.example.cristian.myapplication.ui.bovine.reproductive

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.ActivityReproductiveBovineBinding
import com.example.cristian.myapplication.ui.adapters.ReproductiveBovineAdapter
import com.example.cristian.myapplication.ui.bovine.reproductive.ListZealFragment.Companion.ID_BOVINO
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
    }
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector
}
