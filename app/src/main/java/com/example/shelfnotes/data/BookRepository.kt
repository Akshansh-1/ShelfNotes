package com.example.shelfnotes.data

import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun getBookById(id: Int): Book? {
        return bookDao.getBookById(id)
    }

    suspend fun insertBook(book: Book) {
        bookDao.insertBook(book)
    }

    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(id: Int) {
        bookDao.deleteBook(id)
    }

    fun getPagesForBook(bookId: Int): Flow<List<Page>> {
package com.example.shelfnotes.data

import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {
    val allBooks: Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun getBookById(id: Int): Book? {
        return bookDao.getBookById(id)
    }

    suspend fun insertBook(book: Book) {
        bookDao.insertBook(book)
    }

    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun deleteBook(id: Int) {
        bookDao.deleteBook(id)
    }

    fun getPagesForBook(bookId: Int): Flow<List<Page>> {
        return bookDao.getPagesForBook(bookId)
    }

    suspend fun insertPage(page: Page) {
        bookDao.insertPage(page)
    }

    suspend fun deletePage(pageId: Int) {
        bookDao.deletePage(pageId)
    }
    
    suspend fun updatePage(page: Page) {
        bookDao.updatePage(page)
    }
}
