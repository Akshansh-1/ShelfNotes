package com.example.shelfnotes.ui.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shelfnotes.data.Book
import com.example.shelfnotes.data.BookRepository
import com.example.shelfnotes.data.Page
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository,
    private val bookId: Int
) : ViewModel() {

    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook: StateFlow<Book?> = _currentBook

    val pages: StateFlow<List<Page>> = repository.getPagesForBook(bookId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            _currentBook.value = repository.getBookById(bookId)
            // Initialize pages if empty (Mock logic for now)
            // In a real app, we might check if pages exist and create default ones
        }
    }

    fun savePage(page: Page) {
        viewModelScope.launch {
            repository.insertPage(page)
        }
    }

    fun addPage() {
        viewModelScope.launch {
            val currentPages = pages.value
            val newPageNumber = (currentPages.maxOfOrNull { it.pageNumber } ?: 0) + 1
            val newPage = Page(
                bookId = bookId,
                pageNumber = newPageNumber,
                contentText = "",
                drawingPathsJson = "",
                template = "LINED"
            )
            repository.insertPage(newPage)
        }
    }

    fun deletePage(page: Page) {
        viewModelScope.launch {
            repository.deletePage(page.id)
        }
    }
}

class BookViewModelFactory(
    private val repository: BookRepository,
    private val bookId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookViewModel(repository, bookId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
