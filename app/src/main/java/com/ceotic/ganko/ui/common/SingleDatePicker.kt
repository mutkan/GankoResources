package com.ceotic.ganko.ui.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import android.widget.TextView
import java.util.*


class SingleDatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var txt: TextView? = null

    fun setTextView(txt: TextView) {
        this.txt = txt
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val selectedDate = String.format("%02d", day) + "/" + String.format("%02d", month + 1) + "/" + String.format("%04d", year)
        txt?.text = selectedDate
    }

    companion object {
        fun newInstance(txt: TextView): SingleDatePicker {
            val fragment = SingleDatePicker()
            fragment.setTextView(txt)
            return fragment
        }
    }
}