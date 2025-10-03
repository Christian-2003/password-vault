package de.christian2003.passwordvault.plugin.presentation.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color


/**
 * Draws a background at the bottom of the screen that has the same height as the navigation bar.
 *
 * @param color         Color for the background.
 * @param windowInsets  Window insets must at least contain the bottom insets.
 */
@Composable
fun NavigationBarProtection(
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    windowInsets: WindowInsets
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(windowInsets.asPaddingValues().calculateBottomPadding())
        ) {
            drawRect(
                color = color,
                size = Size(width = size.width, size.height)
            )
        }
    }
}
