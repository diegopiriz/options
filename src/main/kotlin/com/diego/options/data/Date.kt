package com.diego.options.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private val sdf = SimpleDateFormat("yyyy-MM-dd")

class Date(
    private val year: Int,
    private val month: Int,
    private val day: Int
): Comparable<Date> {
    override fun toString(): String {
        val cal = Calendar.getInstance()
        cal.set(YEAR, year)
        cal.set(MONTH, month-1)
        cal.set(DAY_OF_MONTH, day)
        return sdf.format(cal.time)
    }

    fun daysUntil(other: Date): Int {
        val d1 = sdf.parse(toString())
        val d2 = sdf.parse(other.toString())
        return TimeUnit.DAYS.convert(d2.time - d1.time, TimeUnit.MILLISECONDS).toInt()
    }

    override fun compareTo(other: Date): Int {
        return compareValuesBy(this, other, { it.year }, { it.month }, { it.day })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Date

        if (year != other.year) return false
        if (month != other.month) return false
        if (day != other.day) return false

        return true
    }
}

fun today(): Date {
    val cal = Calendar.getInstance()
    return Date(cal.get(YEAR), cal.get(MONTH), cal.get(DAY_OF_MONTH))
}

fun fromSeconds(seconds: Long): Date {
    val cal = Calendar.getInstance()
    cal.time = java.util.Date(seconds * 1000)
    cal.timeZone = TimeZone.getTimeZone("UTC")
    return Date(cal.get(YEAR), cal.get(MONTH)+1, cal.get(DAY_OF_MONTH))
}
