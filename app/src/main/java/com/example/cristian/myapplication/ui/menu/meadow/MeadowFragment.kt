package com.example.cristian.myapplication.ui.menu.meadow

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.databinding.FragmentMeadowBinding
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_meadow.*
import org.w3c.dom.Text

class MeadowFragment : Fragment() {

    lateinit var binding: FragmentMeadowBinding
    var numOfRows = 0
    var numOfColumns = 0
    lateinit var row: TableRow
    val param = TableRow.LayoutParams()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_meadow, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        param.height = -1
        param.width = 200
        param.bottomMargin = 10
        addRowBottom.clicks()
                .subscribe {
                    row = TableRow(this.context)
                    val tv = TextView(activity)
                    numOfRows += 1
                    tv.text = "Pradera $numOfRows"
                    tv.setTextColor(resources.getColor(R.color.white))
                    tv.background = resources.getDrawable(R.drawable.meadow_shape_background)
                    tv.gravity = Gravity.CENTER
                    row.addView(tv,param)
                    gridMeadows.addView(row)
                }

        addRowTop.clicks()
                .subscribe {
                    row = TableRow(this.context)
                    val tv = TextView(activity)
                    numOfRows += 1
                    tv.text = "Pradera $numOfRows"
                    tv.setTextColor(resources.getColor(R.color.white))
                    tv.background = resources.getDrawable(R.drawable.meadow_shape_background)
                    tv.gravity = Gravity.CENTER
                    row.addView(tv,param)
                    gridMeadows.addView(row, 0)
                }


    }

    companion object {
        fun instance(): MeadowFragment = MeadowFragment()
    }
}
