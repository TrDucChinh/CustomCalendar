package com.eco.customcalendar

import android.graphics.Color

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.eco.customcalendar.databinding.ItemCalendarDayBinding
import com.eco.customcalendar.databinding.ItemCalendarHeaderBinding
import java.text.DateFormatSymbols
import java.util.*

class CalendarAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Any> = listOf()

    fun setItems(newItems: List<Any>) {
        items = newItems
        notifyDataSetChanged() // Cập nhật toàn bộ danh sách
    }
    val currentList: List<Any> get() = items


    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is String -> CalendarViewType.HEADER.type
        is CalendarDay -> CalendarViewType.DAY.type
        else -> throw IllegalArgumentException("Unknown item type")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CalendarViewType.HEADER.type -> HeaderViewHolder(ItemCalendarHeaderBinding.inflate(inflater, parent, false))
            CalendarViewType.DAY.type -> DayViewHolder(ItemCalendarDayBinding.inflate(inflater, parent, false), onDayClick)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(items[position] as String)
            is DayViewHolder -> holder.bind(items[position] as CalendarDay)
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(private val binding: ItemCalendarHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: String) {
            binding.tvHeader.text = header
            val sunday = DateFormatSymbols(Locale.getDefault()).shortWeekdays[Calendar.SUNDAY]
            binding.tvHeader.setTextColor(if (header == sunday) Color.RED else Color.BLACK)
        }
    }

    class DayViewHolder(
        private val binding: ItemCalendarDayBinding,
        private val onDayClick: (CalendarDay) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentDay: CalendarDay? = null

        init {
            binding.root.setOnClickListener {
                currentDay?.let(onDayClick)
            }
        }

        fun bind(day: CalendarDay) {
            currentDay = day
            if (day.day == null) {
                hideEmptyDay()
            } else {
                showDay(day)
            }
        }

        private fun hideEmptyDay() {
            binding.apply {
                dayText.visibility = View.INVISIBLE
                circleView.visibility = View.INVISIBLE
                imgNote.visibility = View.INVISIBLE
            }
        }

        private fun showDay(day: CalendarDay) {
            binding.apply {
                dayText.visibility = View.VISIBLE
                circleView.visibility = View.VISIBLE
                dayText.text = day.day.toString()

                dayText.setTextColor(
                    when {
                        day.isSelected -> Color.WHITE
                        day.isSunday -> Color.RED
                        else -> Color.BLACK
                    }
                )
                dayText.setBackgroundResource(
                    if (day.isSelected) R.drawable.bg_pill else android.R.color.transparent
                )

                imgNote.visibility = if (!day.hasNote && day.isSelected) View.VISIBLE else View.INVISIBLE

                circleView.setBackgroundResource(
                    when {
                        day.isSelected && !day.hasNote -> R.drawable.circle_selected
                        day.hasNote -> R.drawable.flower_svgrepo_com
                        else -> R.drawable.circle_default
                    }
                )

                if (day.isSelected && day.hasNote) {
                    val scaleAnimation = ScaleAnimation(
                        1f, 1.1f,
                        1f, 1.1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    )
                    scaleAnimation.duration = 300
                    scaleAnimation.fillAfter = true
                    circleView.startAnimation(scaleAnimation)
                } else {
                    circleView.scaleX = 1f
                    circleView.scaleY = 1f
                }
            }
        }
    }
}


/*
package com.eco.customcalendar

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eco.customcalendar.databinding.ItemCalendarDayBinding
import com.eco.customcalendar.databinding.ItemCalendarHeaderBinding
import java.text.DateFormatSymbols
import java.util.*

class CalendarAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : ListAdapter<Any, RecyclerView.ViewHolder>(CalendarDiffCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is String -> CalendarViewType.HEADER.type
        is CalendarDay -> CalendarViewType.DAY.type
        else -> throw IllegalArgumentException("Unknown item type")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CalendarViewType.HEADER.type -> HeaderViewHolder(ItemCalendarHeaderBinding.inflate(inflater, parent, false))
            CalendarViewType.DAY.type -> DayViewHolder(ItemCalendarDayBinding.inflate(inflater, parent, false), onDayClick)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(getItem(position) as String)
            is DayViewHolder -> holder.bind(getItem(position) as CalendarDay)
        }
    }

    class HeaderViewHolder(private val binding: ItemCalendarHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(header: String) {
            binding.tvHeader.text = header
            val sunday = DateFormatSymbols(Locale.getDefault()).shortWeekdays[Calendar.SUNDAY]
            binding.tvHeader.setTextColor(if (header == sunday) Color.RED else Color.BLACK)
        }
    }

    class DayViewHolder(
        private val binding: ItemCalendarDayBinding,
        private val onDayClick: (CalendarDay) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentDay: CalendarDay? = null

        init {
            binding.root.setOnClickListener {
                currentDay?.let(onDayClick)
            }
        }

        fun bind(day: CalendarDay) {
            currentDay = day

            if (day.day == null) {
                hideEmptyDay()
            } else {
                showDay(day)
            }
        }

        private fun hideEmptyDay() {
            binding.apply {
                dayText.visibility = View.INVISIBLE
                circleView.visibility = View.INVISIBLE
                imgNote.visibility = View.INVISIBLE

            }
        }

        private fun showDay(day: CalendarDay) {
            binding.apply {

                dayText.visibility = View.VISIBLE
                circleView.visibility = View.VISIBLE
                dayText.text = day.day.toString()

                dayText.setTextColor(
                    when {
                        day.isSelected -> Color.WHITE
                        day.isSunday -> Color.RED
                        else -> Color.BLACK
                    }
                )
                dayText.setBackgroundResource(
                    if (day.isSelected) R.drawable.bg_pill else android.R.color.transparent
                )

                imgNote.visibility = if (!day.hasNote && day.isSelected) View.VISIBLE else View.INVISIBLE

                circleView.setBackgroundResource(
                    when {
                        day.isSelected && !day.hasNote -> R.drawable.circle_selected
                        day.hasNote -> R.drawable.flower_svgrepo_com
                        else -> R.drawable.circle_default
                    }
                )

                if (day.isSelected && day.hasNote) {
                    val scaleAnimation = ScaleAnimation(
                        1f, 1.1f,
                        1f, 1.1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    )
                    scaleAnimation.duration = 300
                    scaleAnimation.fillAfter = true
                    circleView.startAnimation(scaleAnimation)
                } else {
                    circleView.scaleX = 1f
                    circleView.scaleY = 1f
                }
            }
        }
    }
}
*/

