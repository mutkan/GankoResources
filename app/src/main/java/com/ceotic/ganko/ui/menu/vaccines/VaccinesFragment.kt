package com.ceotic.ganko.ui.menu.vaccines


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.FragmentVaccinesBinding
import com.ceotic.ganko.ui.adapters.VaccinesPagerAdapter
import com.ceotic.ganko.ui.common.PageChangeListener
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.buildViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.fragment_vaccines.*
import javax.inject.Inject


class VaccinesFragment : Fragment(), HasSupportFragmentInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentVaccinesBinding
    @Inject
    lateinit var adapter: VaccinesPagerAdapter
    private val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vaccines, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vaccinesPager.adapter = adapter
        tabsVaccines.setupWithViewPager(vaccinesPager)
        vaccinesPager.addOnPageChangeListener(PageChangeListener)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    companion object {
        fun instance(): VaccinesFragment = VaccinesFragment()
    }

}
