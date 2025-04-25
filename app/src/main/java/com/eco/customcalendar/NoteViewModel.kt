package com.eco.customcalendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eco.customcalendar.data.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao by lazy {
        AppDatabase.getDatabase(application).noteDao()
    }

    val allNotes = dao.getAllNotes().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    suspend fun getNote(date: Date): Note? {
        return dao.getNoteByDate(date)
    }

    fun saveNote(date: Date, content: String) {
        viewModelScope.launch {
            dao.insert(Note(date = date, content = content))
        }
    }
}
