package com.ceotic.ganko.ui.groups


import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.common.SearchBarActivity
import com.ceotic.ganko.ui.groups.adapters.SelectAdapter
import com.ceotic.ganko.ui.menu.Filter
import com.ceotic.ganko.ui.menu.FilterFragment
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.dialog
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_select_bovine.*
import org.jetbrains.anko.support.v4.startActivityForResult
import javax.inject.Inject

class SelectBovineFragment : Fragment(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: GroupViewModel by lazy { buildViewModel<GroupViewModel>(factory) }

    val createGroup: Boolean by lazy { arguments?.getBoolean(ARG_EDITABLE) ?: false }
    var selecteds: HashMap<String, Boolean> = HashMap()
    val adapter: SelectAdapter = SelectAdapter()

    var filter: Filter = Filter()
    var query: String? = null

    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_bovine, container, false)
    }

    override fun onResume() {
        super.onResume()
        adapter.selecteds = selecteds
        list.adapter = adapter

        dis add FilterFragment.filter
                .doOnNext { filter = it }
                .flatMapSingle { viewModel.listBovines(0, filter, query) }
                .subscribe {
                    adapter.data = it.toMutableList()

                }

        dis add SearchBarActivity.query
                .doOnNext { query = it }
                .flatMapSingle { viewModel.listBovines(0, filter, query) }
                .subscribe {
                    adapter.data = it.toMutableList()
                }

        dis add Observable.just(0)
                .mergeWith(adapter.nextPage)
                .flatMapSingle { viewModel.listBovines(it, filter, query) }
                .subscribe { bovines -> adapter.addData(bovines)}

        dis add btnNext.clicks()
                .subscribe {
                    val keys = selecteds.keys.toTypedArray()
                    if (createGroup) {
                        startActivityForResult<SaveGroupActivity>(REQUEST_SAVE, SaveGroupActivity.EXTRAS_BOVINES to keys)
                    } else {
                        val data = Intent()
                        data.putExtra(SelectActivity.DATA_BOVINES, keys)
                        activity!!.setResult(Activity.RESULT_OK, data)
                        activity!!.finish()
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
                    startActivityForResult<BovineSelectedActivity>(7895, BovineSelectedActivity.EXTRA_SELECTED to selecteds.keys.toTypedArray())
                }

        dis add adapter.onSelectBovine
                .subscribe {
                    val size = selecteds.size
                    number.text = "$size"
                    if (size == 1) showBar()
                    else if (size == 0) hideBar()
                }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 7895 && resultCode == Activity.RESULT_OK) {
            selecteds = HashMap(data!!.getStringArrayExtra(BovineSelectedActivity.DATA_ITEMS).associateBy({ it }, { true }))
            val size = selecteds.size
            number.text = "$size"
            if (size == 0) hideBar()
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
