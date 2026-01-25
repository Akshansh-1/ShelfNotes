package com.example.shelfnotes.ui.bookshelf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shelfnotes.data.Book
import com.example.shelfnotes.ui.components.AddBookDialog
import com.example.shelfnotes.ui.components.BookItem
import com.example.shelfnotes.ui.theme.WoodDark
import com.example.shelfnotes.ui.utils.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BookshelfScreen(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    onAddBook: (String, Int, String) -> Unit,
    onDeleteBook: (Book) -> Unit,
    onToggleFavorite: (Book) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit,
    filterCategory: String,
    onFilterChange: (String) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    val haptic = rememberHapticFeedback()

    if (showAddDialog) {
        AddBookDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, color, category ->
                onAddBook(title, color, category)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(WoodDark)) {
                TopAppBar(
                    title = { Text("My Shelf", color = MaterialTheme.colorScheme.onBackground) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = WoodDark
                    ),
                    actions = {
                        // Sort Button
                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(
                                    Icons.Default.List,
                                    contentDescription = "Sort",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                listOf("Date", "Title", "Category").forEach { sort ->
                                    DropdownMenuItem(
                                        text = { Text(sort) },
                                        onClick = {
                                            onSortChange(sort)
                                            showSortMenu = false
                                        },
                                        leadingIcon = {
                                            if (sortBy == sort) {
                                                Icon(Icons.Default.Check, contentDescription = null)
                                             }
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Dark Mode Toggle
                        IconButton(onClick = onToggleDarkMode) {
                            Icon(
                                Icons.Default.Info, // Placeholder for Dark/Light mode
                                contentDescription = "Toggle Dark Mode",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search books...") },
                            modifier = Modifier
                                .width(200.dp)
                                .padding(end = 8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                )
                
                // Category Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WoodDark)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf("All", "Personal", "Work", "Study", "Ideas", "Journal", "Other")
                    items(categories) { category ->
                        FilterChip(
                            selected = filterCategory == category,
                            onClick = { onFilterChange(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Book")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (books.isEmpty() && searchQuery.isBlank() && filterCategory == "All") {
                // Empty State
                 EmptyState(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(books, key = { it.id }) { book ->
                        // Wrap in AnimatedVisibility for entry animation
                        // Note: In strict Grid contexts, this might cause layout jank if not careful,
                        // but with key it should be okay. 
                        // For simpler staggered effect, we rely on the fact that items are added.
                        // Ideally we'd use Modifier.animateItem() but waiting on that.
                        // We will just use the Shared Element transition for entering details
                        // and standard composition for list.
                        
                        with(sharedTransitionScope) {
                             BookItem(
                                book = book,
                                onClick = { onBookClick(book) },
                                onDelete = { onDeleteBook(book) },
                                onToggleFavorite = { onToggleFavorite(book) },
                                haptic = haptic,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star, // Or a better "Library" icon if available
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your shelf is empty",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to add your first book.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}
