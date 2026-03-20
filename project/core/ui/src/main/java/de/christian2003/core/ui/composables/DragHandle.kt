package de.christian2003.core.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


/**
 * Custom drag handle for a bottom sheet. We use this custom drag handle, because I do not like
 * the visual appearance of the default drag handle.
 *
 * @param modifier  Modifier.
 */
@Composable
fun DragHandle(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .size(
                width = 48.dp,
                height = 4.dp
            )
            .clip(
                RoundedCornerShape(2.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    )
}
