package com.example.cristian.myapplication.ui.menu.vaccines


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentVaccinesBinding
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.putFragment
import com.jakewharton.rxbinding2.support.design.widget.RxBottomNavigationView
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_vaccines.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


class VaccinesFragment : Fragment(), HasSupportFragmentInjector {

    @Inject
    lateinit var injector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentVaccinesBinding
    private val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    private val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_vaccines, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dis add RxBottomNavigationView.itemSelections(vaccinesBottomNavigation)
                .subscribeBy(
                        onNext = {
                            when(it.itemId){
                                R.id.vaccinations -> {putFragment(R.id.vaccinesContainer,VaccinationsFragment.instance())}
                                R.id.nextVaccinations -> {putFragment(R.id.vaccinesContainer,RevaccinationFragment.instance())}
                            }
                        }
                )

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = injector

    companion object {
        fun instance(): VaccinesFragment = VaccinesFragment()
    }

}
