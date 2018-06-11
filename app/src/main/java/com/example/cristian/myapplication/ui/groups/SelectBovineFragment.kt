package com.example.cristian.myapplication.ui.groups


import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.groups.adapters.SelectAdapter
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.dialog
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_select_bovine.*
import org.jetbrains.anko.support.v4.startActivity
import javax.inject.Inject

class SelectBovineFragment : Fragment(), Injectable {


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: GroupViewModel by lazy { buildViewModel<GroupViewModel>(factory) }

    val createGroup: Boolean by lazy { arguments?.getBoolean(ARG_EDITABLE) ?: false }
    val selecteds: HashMap<String, Boolean> = HashMap()
    val adapter: SelectAdapter = SelectAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_bovine, container, false)
    }

    override fun onResume() {
        super.onResume()
        adapter.selecteds = selecteds
        list.adapter = adapter

        viewModel.listBovines(0)
                .subscribe { bovines -> adapter.data = bovines }

        btnNext.clicks()
                .subscribe {
                    if (createGroup) {
                        startActivity<SaveGroupActivity>(SaveGroupActivity.DATA_BOVINES to selecteds)
                    } else {
                        val data = Intent()
                        data.putExtra(SelectActivity.DATA_BOVINES, selecteds.keys.toTypedArray())
                        activity!!.setResult(Activity.RESULT_OK, data)
                    }
                }

        btnClear.clicks()
                .flatMap { dialog(R.string.select_clear, 0) }
                .subscribe {
                    selecteds.clear()
                    number.text = "0"
                    hideBar()
                }

        btnList.clicks()
                .subscribe {
                    startActivity<BovineSelectedActivity>(BovineSelectedActivity.EXTRA_SELECTED to selecteds)
                }

        adapter.onSelectBovine
                .subscribe {
                    val size = selecteds.size
                    number.text = "$size"
                    if (size == 1) showBar()
                    else if (size == 0) hideBar()
                }


    }

    private fun hideBar() {
        TransitionManager.beginDelayedTransition(bottomBar)
        bottomBar.visibility = View.GONE
    }

    private fun showBar() {
        TransitionManager.beginDelayedTransition(bottomBar)
        bottomBar.visibility = View.VISIBLE
    }


    companion object {

        private const val ARG_EDITABLE = "edtiable"

        @JvmStatic
        fun instance(createGroup: Boolean): SelectBovineFragment = SelectBovineFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_EDITABLE, createGroup)
            }
        }
    }


}
