package com.example.cristian.myapplication.ui.menu.management


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentNextManageBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.NextManageAdapter
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.add
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import javax.inject.Inject


class NextManageFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var binding: FragmentNextManageBinding
    @Inject
    lateinit var adapter: NextManageAdapter
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    val from: Date = Date()
    val to: Date by lazy { from.add(Calendar.DATE, 7)!! }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_next_manage, container, false)
        binding.isEmpty = isEmpty
        binding.recyclerNextManageFragment.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getNextManages(from, to)
                .subscribeBy(
                        onNext = {
                            adapter.nextManages = it
                            isEmpty.set(it.isEmpty())
                        }
                )


    }

    companion object {
        fun instance(): NextManageFragment = NextManageFragment()
    }

    /*
    * class ManageFragment : Fragment(),Injectable{

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    private val idFinca: String by lazy { viewModel.getFarmId() }

    @Inject
    lateinit var adapter: ManageBovineAdapter
    lateinit var binding: FragmentManageBinding
    val isEmpty: ObservableBoolean = ObservableBoolean(false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage, container, false)
        binding.isEmpty = isEmpty
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        recyclerListManageFragment.adapter = adapter

        dis add viewModel.getManagement(idFinca)
                .subscribeBy (
                        onSuccess = {
                            isEmpty.set(it.isEmpty())
                            adapter.manage = it
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add btnAddManageFragment.clicks()
                .subscribeByAction(
                        onNext = {
                            startActivity<AddManageActivity>()
                        },
                        onHttpError = {},
                        onError = {}
                )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            toast("Funca !!${data!!.extras["bovines"]}")
        }
    }
    companion object {
        fun instance():ManageFragment = ManageFragment()
    }
}
    *
    * */


}
