package com.example.cristian.myapplication.ui.menu.management

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentManageBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ManageAdapter
import com.example.cristian.myapplication.ui.adapters.ManageBovineAdapter
import com.example.cristian.myapplication.ui.groups.SelectActivity
import com.example.cristian.myapplication.ui.manage.AddManageActivity
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.AppViewModelFactory_Factory
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByAction
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_manage.*
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult

import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class ManageFragment : Fragment(), Injectable{

    @Inject
    lateinit var adapter: ManageAdapter
    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pagerManage.adapter = adapter
        tabsManage.setupWithViewPager(pagerManage)
    }

    companion object {
        fun instance():ManageFragment = ManageFragment()
    }


}
