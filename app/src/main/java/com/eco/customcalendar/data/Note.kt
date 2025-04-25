package com.eco.customcalendar.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val date: Date,
    @ColumnInfo(name = "content")val content: String
)
