package com.example.shelfnotes.ui.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shelfnotes.data.Book
import com.example.shelfnotes.data.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first

class BookshelfViewModel(private val repository: BookRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortBy = MutableStateFlow("Date") // Date, Title, Category
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    private val _filterCategory = MutableStateFlow("All")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    val books: StateFlow<List<Book>> = repository.allBooks
        .combine(_searchQuery) { books, query ->
            if (query.isBlank()) {
                books
            } else {
                // Search in book titles AND page content
                viewModelScope.async {
                    books.filter { book ->
                        // Check title
                        if (book.title.contains(query, ignoreCase = true)) {
                            return@filter true
                        }
                        // Check page content
                        val pages = repository.getPagesForBook(book.id).first()
                        pages.any { page ->
                            page.contentText.contains(query, ignoreCase = true)
                        }
                    }
                }.await()
            }
        }
        .combine(_filterCategory) { books, category ->
            if (category == "All") books
            else books.filter { it.category == category }
        }
        .combine(_sortBy) { books, sort ->
            val sorted = when (sort) {
                "Title" -> books.sortedBy { it.title }
                "Category" -> books.sortedBy { it.category }
                else -> books.sortedByDescending { it.lastModified } // Date
            }
            // Always show favorites first
            sorted.sortedByDescending { it.isFavorite }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
    }

    fun setFilterCategory(category: String) {
        _filterCategory.value = category
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            repository.insertBook(book)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book.id)
        }
    }

    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            repository.updateBook(book.copy(isFavorite = !book.isFavorite))
        }
    }
}

class BookshelfViewModelFactory(private val repository: BookRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookshelfViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookshelfViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
