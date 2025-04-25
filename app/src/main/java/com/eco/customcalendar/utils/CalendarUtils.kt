package com.eco.customcalendar.utils

import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

internal fun isSunday(year: Int, month: Int, day: Int): Boolean {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
}

internal fun isToday(year: Int, month: Int, day: Int): Boolean {
    val today = Calendar.getInstance()
    return today.get(Calendar.YEAR) == year &&
            today.get(Calendar.MONTH) == month &&
            today.get(Calendar.DAY_OF_MONTH) == day
}

internal fun getMonthYearText(year: Int, month: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    val monthName =
        calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: ""
    return "$monthName $year"
}

internal fun getWeekDayHeaders(locale: Locale = Locale.getDefault()): List<String> {
    val symbols = DateFormatSymbols(locale)
    val weekdays = symbols.shortWeekdays

    val mondayIndex = Calendar.MONDAY
    val result = mutableListOf<String>()
    for (i in 0 until 7) {
        val dayIndex = (mondayIndex + i - 1) % 7 + 1
        result.add(weekdays[dayIndex])
    }
    return result
}

internal fun getFirstDayOfWeek(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
}

internal fun getMaxDayOfMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}