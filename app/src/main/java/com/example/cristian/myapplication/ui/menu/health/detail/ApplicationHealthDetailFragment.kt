package com.example.cristian.myapplication.ui.menu.health.detail

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentApplicationHealthDetailBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListHealthBovineAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ApplicationHealthDetailFragment : Fragment(),Injectable {

    //val dosisUnoHealth : String by lazy { arguments!!.getString(DOSIS_UNO_HEALTH) }
@Inject
lateinit var factory: ViewModelProvider.Factory
private val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
val dis: LifeDisposable = LifeDisposable(this)
@Inject
    lateinit var adapter:ListHealthBovineAdapter
    lateinit var binding :FragmentApplicationHealthDetailBinding



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_application_health_detail,container,false)
        binding.listHealth.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dis add viewModel.getHealthApplied()
                .subscribeBy(
                        onSuccess = {
                            adapter.data = it
                        }
                )

    }
    companion object {
       private const val DOSIS_UNO_HEALTH = "dosisUnoHealth"

        fun instance():ApplicationHealthDetailFragment = ApplicationHealthDetailFragment()
    }
}
