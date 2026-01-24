package de.christian2003.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp


/**
 * Draws a background at the bottom of the screen that has the same height as the navigation bar.
 *
 * @param height    Height for the navigation bar protection.
 * @param color     Color for the background.
 */
@Composable
fun NavigationBarProtection(
    height: Dp,
    color: Color = MaterialTheme.colorScheme.surfaceContainer.copy(0.5f)
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            drawRect(
                color = color,
                size = Size(width = size.width, size.height)
            )
        }
    }
}
