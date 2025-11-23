package com.example.shelfnotes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageTurnEffect(
    pagerState: PagerState,
    pageCount: Int,
    content: @Composable (Int) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Calculate the absolute offset for the current page from the
                    // scroll position. We use the absolute value which allows us to mirror
                    // any effects for both directions
                    val pageOffset = (
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

                    // We animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                    
                    // 3D Rotation effect
                    rotationY = lerp(
                        start = 0f,
                        stop = 45f, // Rotate up to 45 degrees
                        fraction = pageOffset.coerceIn(0f, 1f)
                    ) * if (pagerState.currentPage > page) -1 else 1
                    
                    // Translation to give depth feel
                    translationX = pageOffset * size.width * 0.1f
                    
                    cameraDistance = 8 * density
                }
        ) {
            content(page)
        }
    }
}
