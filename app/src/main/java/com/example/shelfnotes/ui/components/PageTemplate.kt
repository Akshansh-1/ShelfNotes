package com.example.shelfnotes.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp

@Composable
fun PageTemplate(template: String) {
    when (template) {
        "BLANK" -> {
            // No background pattern
        }
        "LINED" -> {
            LinedTemplate()
        }
        "GRID" -> {
            GridTemplate()
        }
        "DOTTED" -> {
            DottedTemplate()
        }
    }
}

@Composable
fun LinedTemplate() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val lineSpacing = 31.dp.toPx()
        var y = 40.dp.toPx()
        
        while (y < size.height) {
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            y += lineSpacing
        }
    }
}

@Composable
fun GridTemplate() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSpacing = 30.dp.toPx()
        
        // Horizontal lines
        var y = gridSpacing
        while (y < size.height) {
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            y += gridSpacing
        }
        
        // Vertical lines
        var x = gridSpacing
        while (x < size.width) {
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
            x += gridSpacing
        }
    }
}

@Composable
fun DottedTemplate() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val dotSpacing = 20.dp.toPx()
        val dotRadius = 1.5f
        
        var y = dotSpacing
        while (y < size.height) {
            var x = dotSpacing
            while (x < size.width) {
                drawCircle(
                    color = Color.LightGray,
                    radius = dotRadius,
                    center = Offset(x, y)
                )
                x += dotSpacing
            }
            y += dotSpacing
        }
    }
}
