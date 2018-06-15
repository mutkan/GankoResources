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
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.dialog
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_select_bovine.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import javax.inject.Inject

class SelectBovineFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: GroupViewModel by lazy { buildViewModel<GroupViewModel>(factory) }

    val createGroup: Boolean by lazy { arguments?.getBoolean(ARG_EDITABLE) ?: false }
    val selecteds: HashMap<String, Boolean> = HashMap()
    val adapter: SelectAdapter = SelectAdapter()

    val dis:LifeDisposable = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_bovine, container, false)
    }

    override fun onResume() {
        super.onResume()
        adapter.selecteds = selecteds
        list.adapter = adapter

        dis add viewModel.listBovines(0)
                .subscribe {
                    bovines -> adapter.data = bovines }

        dis add btnNext.clicks()
                .subscribe {
                    val keys = selecteds.keys.toTypedArray()
                    if (createGroup) {
                        startActivityForResult<SaveGroupActivity>(REQUEST_SAVE,SaveGroupActivity.EXTRAS_BOVINES to keys)
                    } else {
                        val data = Intent()
                        data.putExtra(SelectActivity.DATA_BOVINES, keys)
                        activity!!.setResult(Activity.RESULT_OK, data)
                    }
                }

        dis add btnClear.clicks()
                .flatMap { dialog(R.string.select_clear, 0) }
                .subscribe {
                    adapter.notifyDataSetChanged()
                    selecteds.clear()
                    number.text = "0"
                    hideBar()
                }

        dis add btnList.clicks()
                .subscribe {
                    startActivity<BovineSelectedActivity>(BovineSelectedActivity.EXTRA_SELECTED to selecteds)
                }

        dis add adapter.onSelectBovine
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
        bottomBar.translationY = resources.getDimension(R.dimen.select_bar)
    }

    private fun showBar() {
        TransitionManager.beginDelayedTransition(bottomBar)
        bottomBar.visibility = View.VISIBLE
        bottomBar.translationY = 0f
    }


    companion object {

        private const val ARG_EDITABLE = "edtiable"
        const val REQUEST_SAVE = 101

        @JvmStatic
        fun instance(createGroup: Boolean): SelectBovineFragment = SelectBovineFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_EDITABLE, createGroup)
            }
        }
    }


}
