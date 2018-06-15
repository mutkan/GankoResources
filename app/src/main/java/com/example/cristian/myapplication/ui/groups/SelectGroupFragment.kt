package com.example.cristian.myapplication.ui.groups

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.groups.adapters.GroupsAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.dialog
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_select_group.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject


class SelectGroupFragment : Fragment(), Injectable {

    private val editable: Boolean by lazy { arguments?.getBoolean(ARG_EDITABLE) ?: false }
    private val adapter: GroupsAdapter = GroupsAdapter()
    private val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: GroupViewModel by lazy { buildViewModel<GroupViewModel>(factory) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_group, container, false)
    }

    override fun onResume() {
        super.onResume()
        fab.visibility = if (editable) View.VISIBLE else View.GONE

        dis add fab.clicks()
                .subscribe { startActivity<AddGroupActivity>() }

        adapter.editable = editable
        list.adapter = adapter

        dis add adapter.onClick
                .subscribe { group ->
                    if (editable) {
                        startActivity<SaveGroupActivity>(SaveGroupActivity.EXTRAS_GROUP to group)
                    } else {
                        val intent = Intent()
                        intent.putExtra(SelectActivity.DATA_GROUP, group)
                        activity!!.setResult(Activity.RESULT_OK, intent)
                    }
                }

        dis add adapter.onDelete
                .flatMap { dialog(R.string.remove_group, it) }
                .flatMapSingle { viewModel.remove(it._id!!) }
                .subscribe { toast(R.string.group_removed) }

        if (editable) {
            dis add viewModel.listObservable()
                    .subscribe { adapter.data = it }
        } else {
            dis add viewModel.list()
                    .subscribe { groups -> adapter.data = groups }
        }
    }

    companion object {

        private const val ARG_EDITABLE = "edtiable"

        @JvmStatic
        fun instance(editable: Boolean): SelectGroupFragment = SelectGroupFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_EDITABLE, editable)
            }
        }
    }
}
