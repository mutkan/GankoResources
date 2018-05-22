package com.example.cristian.myapplication.ui.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.TemplateSelectFeedBinding
import com.example.cristian.myapplication.util.inflate
import javax.inject.Inject

class ListFeedSelectBovinesAdapter @Inject constructor(): RecyclerView.Adapter<ListFeedSelectBovinesAdapter.ListFeedSelectBovinesHolder>(){

    var selectAll:Boolean= false
    var feedSelectbovines: List<Bovino> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ListFeedSelectBovinesHolder, position: Int) {
    var bovino:Bovino = feedSelectbovines.get(position)
     holder.binding!!.selectFeedBtn.setOnCheckedChangeListener(null)
     holder.binding!!.selectFeedBtn.setChecked(bovino.seleccionado!!)
     holder.binding!!.selectFeedBtn.setOnCheckedChangeListener { compoundButton, b ->
         if (b) bovino.seleccionado= true
         else{ bovino.seleccionado = false
               selectAll= false

         }
     }
     holder.binding!!.selectFeedBtn.setChecked(bovino.seleccionado!!)
        //Select all
        if (selectAll) holder.binding!!.selectFeedBtn.setChecked(true)
        else holder.binding!!.selectFeedBtn.setChecked(false)


    }

    override fun getItemCount(): Int = feedSelectbovines.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListFeedSelectBovinesHolder
            = ListFeedSelectBovinesHolder(parent.inflate(R.layout.template_select_feed))


    class ListFeedSelectBovinesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding : TemplateSelectFeedBinding? = DataBindingUtil.bind(itemView)

    }

    fun selectAll(n:Int){
        selectAll = n==1
    notifyDataSetChanged()
    }
}
