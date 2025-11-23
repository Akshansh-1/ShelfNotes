package com.example.shelfnotes.data

package com.example.shelfnotes.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Page(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: Int,
    val pageNumber: Int,
    val contentText: String = "",
    val drawingPathsJson: String = "", // Store paths as JSON string for simplicity
    val template: String = "LINED" // BLANK, LINED, GRID, DOTTED
)
