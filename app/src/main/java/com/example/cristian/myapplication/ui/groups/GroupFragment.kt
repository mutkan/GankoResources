package com.example.cristian.myapplication.ui.groups

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.databinding.FragmentGroupBinding
import com.example.cristian.myapplication.util.LifeDisposable
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_group.*
import org.jetbrains.anko.support.v4.startActivityForResult

class GroupFragment : Fragment() {

    private var group: Group? = null
    private lateinit var binding: FragmentGroupBinding
    private var bovines: List<String>? = null
    private val dis: LifeDisposable = LifeDisposable(this)
    private var color: Int = 0
    val ids: PublishSubject<List<String>> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            group = getParcelable(ARG_GROUP)
            bovines = getStringArray(ARG_BOVINES).toList()
            color = getInt(ARG_COLORS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentGroupBinding.inflate(inflater, container, false)
        binding.grouped = group != null
        binding.group = group
        binding.bovines = bovines?.size ?: 0
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dis add selection.clicks()
                .subscribe {
                    val list: Array<String> = if (bovines == null) group!!.bovines.toTypedArray()
                    else bovines!!.toTypedArray()

                    startActivityForResult<BovineSelectedActivity>(1034, BovineSelectedActivity.EXTRA_COLOR to color,
                            BovineSelectedActivity.EXTRA_SELECTED to list,
                            BovineSelectedActivity.EXTRA_EDITABLE to (bovines != null))
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1034 && resultCode == Activity.RESULT_OK) {
            bovines = data!!.getStringArrayExtra(BovineSelectedActivity.DATA_ITEMS).toList()
            binding.bovines = bovines!!.size
            ids.onNext(bovines!!)
        }
    }

    companion object {

        private val ARG_GROUP = "group"
        private val ARG_BOVINES = "bovines"
        private val ARG_COLORS = "colors"

        fun instance(color: Int, group: Group): GroupFragment = GroupFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_GROUP, group)
                putInt(ARG_COLORS, color)
            }
        }

        fun instance(color: Int, bovines: List<String>): GroupFragment = GroupFragment().apply {
            arguments = Bundle().apply {
                putStringArray(ARG_BOVINES, bovines.toTypedArray())
                putInt(ARG_COLORS, color)
            }
        }

        fun instance(color: Int, data: Intent?): GroupFragment{
            val group:Group? = data?.extras?.getParcelable(SelectActivity.DATA_GROUP)
            val bovines:List<String>? = data?.extras?.getStringArray(SelectActivity.DATA_BOVINES)
                    ?.toList()
            return if(group == null) instance(color, bovines!!)
            else instance(color, group)
        }



    }

}
