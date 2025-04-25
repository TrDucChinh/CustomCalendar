package com.eco.customcalendar

import androidx.recyclerview.widget.DiffUtil

class CalendarDiffCallback : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return if (oldItem is String && newItem is String) {
            oldItem == newItem
        } else if (oldItem is CalendarDay && newItem is CalendarDay) {
            oldItem.day == newItem.day
        } else false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return oldItem == newItem
    }
}
