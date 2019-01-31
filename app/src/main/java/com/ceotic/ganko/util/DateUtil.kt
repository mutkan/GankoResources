package com.ceotic.ganko.util

import java.util.*

fun processDates(from: Date?, to: Date?, month: Int?, year: Int?): Pair<Date?, Date?> = when {
    month != null -> {
        val calendar: Calendar = Calendar.getInstance()
        val fromDate: Date = calendar.apply {
            set(year!!, month, 1, 11, 59, 59)
            add(Calendar.DATE, -1)
        }.time
        val m = if (month < 11) month + 1 else 0
        val a = if (m == 0) year!! + 1 else year!!
        val toDate: Date = calendar.apply {
            set(a, m, 1, 0, 0, 0)
        }.time
        fromDate to toDate
    }
    from != null -> from to to
    else -> Pair<Date?, Date?>(null, null)
}

fun Date.addCurrentHour(){
    val calendar = Calendar.getInstance()
    add(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
    add(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
}

fun calculateNotifyTime(frequency:Long, units:String):Long{
    val proxTime = when (units) {
        "Horas" -> frequency
        "Días" -> frequency * 24
        "Meses" -> frequency * 24 * 30
        else -> frequency * 24 * 30 * 12
    }
    return when (proxTime) {
        in 0..2 -> proxTime
        in 3..10 -> proxTime - 1
        in 11..36 -> proxTime - 3
        else -> proxTime - 24
    }
}

