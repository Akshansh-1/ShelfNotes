package com.example.shelfnotes.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shelfnotes.data.Book
import com.example.shelfnotes.ui.components.PageTurnEffect
import com.example.shelfnotes.ui.theme.PaperCream

import com.example.shelfnotes.ui.book.BookViewModel

import com.example.shelfnotes.ui.components.PageTemplate

import com.example.shelfnotes.data.Page

import com.example.shelfnotes.ui.components.DrawingController
import com.example.shelfnotes.ui.components.DrawingCanvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
fun BookScreen(
    bookId: Int,
    onClose: () -> Unit,
    viewModel: BookViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val pages by viewModel.pages.collectAsState()
    val currentBook by viewModel.currentBook.collectAsState()
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addPage() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Page")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
             if (pages.isEmpty()) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     Text("No pages. Tap + to add one.")
                 }
             } else {
                 HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
                     val page = pages[pageIndex]
                     BookPage(
                         page = page,
                         onSave = { updatedPage -> viewModel.savePage(updatedPage) }
                     )
                 }
             }
             
             // Close button (Overlay)
             IconButton(
                 onClick = onClose,
                 modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
             ) {
                 Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
             }
        }
    }
}

@Composable
fun BookPage(
    page: Page,
    onSave: (Page) -> Unit
) {
    val drawingController = remember { DrawingController() }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableStateOf(5f) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showStrokeMenu by remember { mutableStateOf(false) }
    var isEraserMode by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    val drawingColors = listOf(
        Color.Black,
        Color.Red,
        Color.Blue,
        Color.Green,
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFFFFEB3B), // Yellow
        Color(0xFF795548)  // Brown
    )

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Page") },
            text = { Text("Are you sure you want to clear all drawings on this page?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        drawingController.clearAll()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Content area for writing/drawing
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp, bottom = 60.dp) // Space for top and bottom toolbars
        ) {
            // Page Template Background
            PageTemplate(template = page.template)
            
            // Text Editor Layer
            BasicTextField(
                value = page.contentText,
                onValueChange = { newText ->
                    onSave(page.copy(contentText = newText))
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 31.sp
                ),
                modifier = Modifier.fillMaxSize(),
                decorationBox = { innerTextField ->
                    if (page.contentText.isEmpty()) {
                        Text(
                            text = "Start writing...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
                }
            )
            
            // Drawing Layer
            DrawingCanvas(
                modifier = Modifier.fillMaxSize(),
                controller = drawingController,
                currentColor = if (isEraserMode) Color.White else currentColor,
                currentStrokeWidth = if (isEraserMode) 20f else currentStrokeWidth
            )
        }

        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Page ${page.pageNumber}",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Row {
                IconButton(onClick = { drawingController.undo() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Undo")
                }
                IconButton(onClick = { drawingController.redo() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Redo")
                }
                IconButton(onClick = { showClearDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Page")
                }
            }
        }

        // Bottom Drawing Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pen/Eraser Toggle
            IconButton(
                onClick = { isEraserMode = !isEraserMode },
                modifier = Modifier
                    .background(
                        if (isEraserMode) MaterialTheme.colorScheme.primaryContainer
                        else Color.Transparent,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    if (isEraserMode) Icons.Default.Edit else Icons.Default.Edit,
                    contentDescription = if (isEraserMode) "Switch to Pen" else "Switch to Eraser",
                    tint = if (isEraserMode) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
                )
            }

            // Color Picker (disabled in eraser mode)
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(enabled = !isEraserMode) { showColorPicker = true }
                        .alpha(if (isEraserMode) 0.5f else 1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(currentColor, CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Color", style = MaterialTheme.typography.bodySmall)
                }
                
                DropdownMenu(
                    expanded = showColorPicker,
                    onDismissRequest = { showColorPicker = false }
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        drawingColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                                    .background(color, CircleShape)
                                    .border(
                                        width = if (color == currentColor) 3.dp else 1.dp,
                                        color = if (color == currentColor) MaterialTheme.colorScheme.primary else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        currentColor = color
                                        isEraserMode = false
                                        showColorPicker = false
                                    }
                            )
                        }
                    }
                }
            }

            // Stroke Width Selector (disabled in eraser mode)
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(enabled = !isEraserMode) { showStrokeMenu = true }
                        .alpha(if (isEraserMode) 0.5f else 1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Brush Size")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${currentStrokeWidth.toInt()}px", style = MaterialTheme.typography.bodySmall)
                }
                
                DropdownMenu(
                    expanded = showStrokeMenu,
                    onDismissRequest = { showStrokeMenu = false }
                ) {
                    listOf(2f, 5f, 10f, 15f, 20f).forEach { width ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(width.dp)
                                            .background(Color.Black)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${width.toInt()}px")
                                }
                            },
                            onClick = {
                                currentStrokeWidth = width
                                showStrokeMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}
