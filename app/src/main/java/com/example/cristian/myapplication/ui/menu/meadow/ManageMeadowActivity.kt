package com.example.cristian.myapplication.ui.menu.meadow

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.databinding.ActivityManageMeadowBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ManageMeadowAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.fixColor
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
