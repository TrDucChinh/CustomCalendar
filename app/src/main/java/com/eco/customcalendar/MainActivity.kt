package com.eco.customcalendar

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.eco.customcalendar.data.Note
import com.eco.customcalendar.databinding.ActivityMainBinding
import com.eco.customcalendar.utils.getFirstDayOfWeek
import com.eco.customcalendar.utils.getMaxDayOfMonth
import com.eco.customcalendar.utils.getMonthYearText
import com.eco.customcalendar.utils.getWeekDayHeaders
import com.eco.customcalendar.utils.isSunday
import com.eco.customcalendar.utils.isToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var calendarAdapter: CalendarAdapter
    private var selectedDate: CalendarDay? = null
    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private val noteViewModel by lazy {
        ViewModelProvider(this)[NoteViewModel::class.java]
    }
    private var notesForDays: List<Note> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupWindowInsetsListener()
        setupCalendar()

        binding.btnPrev.setOnClickListener {
            changeMonth(-1)
        }

        binding.btnNext.setOnClickListener {
            changeMonth(1)
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                noteViewModel.allNotes.collect {
                    withContext(Dispatchers.Main) {
                        notesForDays = it
                        updateCalendar()
                    }
                }
            }
        }

    }

    private fun setupWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom + imeHeight
            )
            insets
        }
    }

    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter { clickedDay ->
            onDateSelected(clickedDay)
        }
        binding.calendarRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarAdapter
        }
    }

    private fun onDateSelected(clickedDay: CalendarDay) {
        if (clickedDay.day != null &&
            selectedDate?.day == clickedDay.day &&
            selectedDate?.month == clickedDay.month &&
            selectedDate?.year == clickedDay.year
        ) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, clickedDay.year)
                set(Calendar.MONTH, clickedDay.month)
                set(Calendar.DAY_OF_MONTH, clickedDay.day)
            }
            showQuickNoteBottomSheet(calendar.time)
            return
        }
        selectedDate = clickedDay
        val updatedItems = calendarAdapter.currentList.map {
            if (it is CalendarDay) {
                it.copy(
                    isSelected = it.day == clickedDay.day &&
                            it.month == clickedDay.month &&
                            it.year == clickedDay.year
                )
            } else it
        }
        calendarAdapter.setItems(updatedItems)
//        calendarAdapter.submitList(updatedItems) list adapter
    }

    private fun showQuickNoteBottomSheet(date: Date) {
        BottomSheet(date) { selectedDate, content ->
            noteViewModel.saveNote(selectedDate, content)
            updateCalendar()
        }.show(supportFragmentManager, "NoteBottomSheet")
    }

    private fun changeMonth(offset: Int) {
        currentMonth += offset
        if (currentMonth > 11) {
            currentMonth = 0
            currentYear++
        } else if (currentMonth < 0) {
            currentMonth = 11
            currentYear--
        }
        updateCalendar()
    }

    private fun updateCalendar() {
        val calendarItems = generateCalendarWithHeader(currentYear, currentMonth)
        val updatedItems = calendarItems.map {
            if (it is CalendarDay) {
                val date = getDateFromDay(it)
                val hasNote = notesForDays.any { note ->
                    val noteCalendar = Calendar.getInstance().apply { time = note.date }
                    val selectedDateCalendar = Calendar.getInstance().apply { time = date }
                    val isSameDay =
                        noteCalendar.get(Calendar.YEAR) == selectedDateCalendar.get(Calendar.YEAR) &&
                                noteCalendar.get(Calendar.MONTH) == selectedDateCalendar.get(
                            Calendar.MONTH
                        ) &&
                                noteCalendar.get(Calendar.DAY_OF_MONTH) == selectedDateCalendar.get(
                            Calendar.DAY_OF_MONTH
                        )
                    isSameDay
                }
                it.copy(hasNote = hasNote)
            } else it
        }
        binding.tvMonthYear.text = getMonthYearText(currentYear, currentMonth)
        calendarAdapter.setItems(updatedItems) //
//        calendarAdapter.submitList(updatedItems) // list adapter
    }


    private fun generateCalendarWithHeader(year: Int, month: Int): List<Any> {
        val calendarItems = mutableListOf<Any>()
        val weekHeaders = getWeekDayHeaders(Locale.getDefault())
        calendarItems.addAll(weekHeaders)

        val firstDayOfWeek = getFirstDayOfWeek(year, month)
        val maxDay = getMaxDayOfMonth(year, month)
        addEmptyDays(calendarItems, firstDayOfWeek)
        addCalendarDays(calendarItems, year, month, maxDay)
        return calendarItems
    }

    private fun addEmptyDays(calendarItems: MutableList<Any>, firstDayOfWeek: Int) {
        repeat(firstDayOfWeek) {
            calendarItems.add(CalendarDay(null))
        }
    }

    private fun addCalendarDays(
        calendarItems: MutableList<Any>,
        year: Int,
        month: Int,
        maxDay: Int
    ) {
        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH)
        val currentMonth = today.get(Calendar.MONTH)
        val currentYear = today.get(Calendar.YEAR)

        for (day in 1..maxDay) {
            val isToday = isToday(year, month, day)
            val isSunday = isSunday(year, month, day)

            val isSelected = when {
                selectedDate != null && day == selectedDate?.day && month == selectedDate?.month && year == selectedDate?.year -> true
                day == currentDay && month == currentMonth && year == currentYear && selectedDate == null -> true
                else -> false
            }
            val calendarDay = CalendarDay(
                day = day,
                isToday = isToday,
                isSunday = isSunday,
                isSelected = isSelected,
                month = month,
                year = year
            )
            calendarItems.add(calendarDay)
        }
    }

    private fun getDateFromDay(calendarDay: CalendarDay): Date {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, calendarDay.year)
            set(Calendar.MONTH, calendarDay.month)
            set(Calendar.DAY_OF_MONTH, calendarDay.day ?: 1)
        }
        return calendar.time
    }
}