package com.example.shelfnotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val colorArgb: Int,
    val lastModified: Long = System.currentTimeMillis(),
    val category: String = "Personal", // Personal, Work, Study, Ideas, etc.
    val isFavorite: Boolean = false
) {
    val color: Color
        get() = Color(colorArgb)
}
