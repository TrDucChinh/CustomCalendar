package com.eco.customcalendar

data class CalendarDay(
    val day: Int?,
    val isToday: Boolean = false,
    val isSunday: Boolean = false,
    val isSelected: Boolean = false,
    val month: Int = -1,
    val year: Int = -1,
    val hasNote: Boolean = false,
)
