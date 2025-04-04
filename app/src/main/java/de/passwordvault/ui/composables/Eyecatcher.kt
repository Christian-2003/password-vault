package de.passwordvault.ui.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * Displays an eyecatcher to the user using a pulsating effect.
 *
 * @param eyecatcherDefaults    Defaults for the eyecatcher.
 */
@Composable
fun Eyecatcher(
    eyecatcherDefaults: EyecatcherDefaults = EyecatcherDefaults.helpEyecatcherDefaults
) {
    val infiniteTransition: InfiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .size(eyecatcherDefaults.size)
            .scale(scale)
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = CircleShape
            )
    )
}


/**
 * Implements defaults for the eyecatcher.
 *
 * @param size  Size for the eyecatcher.
 */
class EyecatcherDefaults(
    val size: Dp = 12.dp
) {

    companion object {

        /**
         * Defaults for the help eyecatcher.
         */
        val helpEyecatcherDefaults = EyecatcherDefaults()

    }

}
