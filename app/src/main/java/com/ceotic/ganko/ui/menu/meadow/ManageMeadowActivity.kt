package com.ceotic.ganko.ui.menu.meadow

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.ActivityManageMeadowBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ManageMeadowAdapter
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.fixColor
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_manage_meadow.*
import javax.inject.Inject

class ManageMeadowActivity : AppCompatActivity(), Injectable, HasSupportFragmentInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    val idMeadow: String by lazy { intent.getStringExtra(MEADOWID) }
    lateinit var binding: ActivityManageMeadowBinding
    lateinit var adapter: ManageMeadowAdapter

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }

    val dis = LifeDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_meadow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fixColor(10)


        manageTab.setupWithViewPager(managePager)
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getMeadow(idMeadow)
                .subscribe {
                    title = "Administrar pradera "+it.identificador
                    adapter = ManageMeadowAdapter(this, supportFragmentManager, it)
                    managePager.adapter = adapter
                }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        const val MEADOWID = "meadow"
    }
}
