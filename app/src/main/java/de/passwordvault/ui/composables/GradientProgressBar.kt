package de.passwordvault.ui.composables

import android.graphics.drawable.shapes.RoundRectShape
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import de.passwordvault.R
import de.passwordvault.ui.theme.LocalPasswordVaultColors


/**
 * Displays an animated progress bar with a gradient.
 *
 * @param progress          Progress to display.
 * @param colors            List of colors to use for gradient.
 * @param modifier          Modifier.
 * @param durationMillis    Animation duration in milliseconds.
 * @param height            Height for the progress bar.
 */
@Composable
fun GradientProgressBar(
    progress: Float,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000,
    height: Dp = dimensionResource(R.dimen.progress_bar_thickness_large)
) {
    var startProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(progress) {
        startProgress = progress
    }

    val animatedProgress: Float by animateFloatAsState(
        targetValue = startProgress,
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = EaseInOut
        )
    )

    Box(
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_l)))
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            val brush = Brush.horizontalGradient(colors)
            drawRoundRect(
                brush = brush,
                size = size.copy(width = size.width * animatedProgress),
                cornerRadius = CornerRadius(100f, 100f)
            )
        }
    }
}
