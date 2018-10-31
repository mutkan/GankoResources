package com.ceotic.ganko.data.models

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker

import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var mChosenDate: Int = 0

    internal var cur = 0


    override fun onCreateDialog(savedInstanceState: Bundle): Dialog? {

        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val bundle = this.arguments
        if (bundle != null) {
            mChosenDate = bundle.getInt("DATE", 1)
        }



        when (mChosenDate) {

            START_DATE -> {
                cur = START_DATE
                return DatePickerDialog(activity, this, year, month, day)
            }

            END_DATE -> {
                cur = END_DATE
                return DatePickerDialog(activity, this, year, month, day)
            }
        }
        return null
    }


    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {

        if (cur == START_DATE) {
            // set selected date into textview
            Log.v("Date Début", "Date1 : " + StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "))
        } else {
            Log.v("Date fin", "Date2 : " + StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "))
        }
    }

    companion object {

        internal val START_DATE = 1
        internal val END_DATE = 2
    }
}