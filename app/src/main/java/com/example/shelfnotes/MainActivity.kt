package com.example.shelfnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shelfnotes.data.Book
import com.example.shelfnotes.ui.book.BookScreen
import com.example.shelfnotes.ui.bookshelf.BookshelfScreen
import com.example.shelfnotes.ui.theme.ShelfNotesTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shelfnotes.data.AppDatabase
import com.example.shelfnotes.data.BookRepository
import com.example.shelfnotes.ui.book.BookViewModel
import com.example.shelfnotes.ui.book.BookViewModelFactory
import com.example.shelfnotes.ui.bookshelf.BookshelfViewModel
import com.example.shelfnotes.ui.bookshelf.BookshelfViewModelFactory

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            
            ShelfNotesTheme(darkTheme = isDarkMode) {
                val context = LocalContext.current
                val database = remember { AppDatabase.getDatabase(context) }
                val repository = remember { BookRepository(database.bookDao()) }
                
                ShelfNotesApp(
                    repository = repository,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ShelfNotesApp(
    repository: BookRepository,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val navController = rememberNavController()
    
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = "bookshelf",
            enterTransition = { slideInHorizontally { it } },
            exitTransition = { slideOutHorizontally { -it } },
            popEnterTransition = { slideInHorizontally { -it } },
            popExitTransition = { slideOutHorizontally { it } }
        ) {
            composable("bookshelf") {
                val viewModel: BookshelfViewModel = viewModel(
                    factory = BookshelfViewModelFactory(repository)
                )
                val books by viewModel.books.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val sortBy by viewModel.sortBy.collectAsState()
                val filterCategory by viewModel.filterCategory.collectAsState()
                
                BookshelfScreen(
                    books = books,
                    onBookClick = { book ->
                        navController.navigate("book/${book.id}")
                    },
                    onAddBook = { title, color, category ->
                        val newBook = Book(
                            title = title,
                            colorArgb = color,
                            lastModified = System.currentTimeMillis(),
                            category = category
                        )
                        viewModel.addBook(newBook)
                    },
                    onDeleteBook = { book ->
                        viewModel.deleteBook(book)
                    },
                    onToggleFavorite = { book ->
                        viewModel.toggleFavorite(book)
                    },
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    sortBy = sortBy,
                    onSortChange = viewModel::setSortBy,
                    filterCategory = filterCategory,
                    onFilterChange = viewModel::setFilterCategory,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            composable(
                route = "book/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
                val viewModel: BookViewModel = viewModel(
                    factory = BookViewModelFactory(repository, bookId)
                )
                
                BookScreen(
                    bookId = bookId,
                    onClose = { navController.popBackStack() },
                    viewModel = viewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
        }
    }
}


