package com.example.shelfnotes.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize

import androidx.compose.runtime.mutableStateListOf

class DrawingController {
    private val _paths = mutableStateListOf<PathState>()
    val paths: List<PathState> get() = _paths

    private val _undoStack = mutableStateListOf<PathState>()

    fun addPath(path: PathState) {
        _paths.add(path)
        _undoStack.clear() // Clear redo stack on new action
    }

    fun undo() {
        if (_paths.isNotEmpty()) {
            val last = _paths.removeLast()
            _undoStack.add(last)
        }
    }

    fun redo() {
        if (_undoStack.isNotEmpty()) {
            val last = _undoStack.removeLast()
            _paths.add(last)
        }
    }

    fun clearAll() {
        _paths.clear()
        _undoStack.clear()
    }
}

data class PathState(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun DrawingCanvas(
    modifier: Modifier = Modifier,
    controller: DrawingController,
    currentColor: Color = Color.Black,
    currentStrokeWidth: Float = 5f
) {
    // Bitmap to cache the drawing
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    // Canvas to draw onto the bitmap
    val bitmapCanvas = remember(bitmap) {
        bitmap?.let { Canvas(it) }
    }
    
    // Temporary path being drawn
    val tempPath = remember { Path() }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    // Paint for drawing onto the bitmap
    val paint = remember(currentColor, currentStrokeWidth) {
        Paint().apply {
            color = currentColor
            strokeWidth = currentStrokeWidth
            style = PaintingStyle.Stroke
            strokeCap = StrokeCap.Round
            strokeJoin = StrokeJoin.Round
        }
    }

    // Rebuild bitmap when paths change (Undo/Redo)
    LaunchedEffect(controller.paths.size) {
        bitmap?.let { b ->
            val canvas = Canvas(b)
            // Clear canvas
            val clearPaint = Paint().apply { blendMode = androidx.compose.ui.graphics.BlendMode.Clear }
            canvas.drawRect(0f, 0f, b.width.toFloat(), b.height.toFloat(), clearPaint)
            
            // Redraw all paths
            controller.paths.forEach { pathState ->
                val pathPaint = Paint().apply {
                    color = pathState.color
                    strokeWidth = pathState.strokeWidth
                    style = PaintingStyle.Stroke
                    strokeCap = StrokeCap.Round
                    strokeJoin = StrokeJoin.Round
                }
                canvas.drawPath(pathState.path, pathPaint)
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                if (size.width > 0 && size.height > 0) {
                    if (bitmap == null || bitmap!!.width != size.width || bitmap!!.height != size.height) {
                        bitmap = ImageBitmap(size.width, size.height)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        tempPath.moveTo(offset.x, offset.y)
                        currentPosition = offset
                    },
                    onDrag = { change, _ ->
                        val newPoint = change.position
                        tempPath.lineTo(newPoint.x, newPoint.y)
                        currentPosition = newPoint
                    },
                    onDragEnd = {
                        // Add to controller
                        controller.addPath(
                            PathState(
                                Path().apply { addPath(tempPath) },
                                currentColor,
                                currentStrokeWidth
                            )
                        )
                        tempPath.reset()
                        currentPosition = Offset.Unspecified
                    }
                )
            }
    ) {
        // Draw the cached bitmap
        bitmap?.let { b ->
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawBitmap(b.asAndroidBitmap(), 0f, 0f, null)
            }
        }
        
        // Draw current path
        if (currentPosition != Offset.Unspecified) {
            drawPath(
                path = tempPath,
                color = currentColor,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = currentStrokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
