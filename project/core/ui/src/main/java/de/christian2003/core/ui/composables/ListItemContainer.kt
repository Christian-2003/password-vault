package de.christian2003.core.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.R


/**
 * Composable displays the container around a list item.
 *
 * @param isFirst           Whether this is the first list item.
 * @param isLast            Whether this is the last list item.
 * @param modifier          Modifier.
 * @param containerColor    Color for the container.
 * @param isSelected        Whether the list item is selected.
 * @param content           Content of the list item.
 */
@Composable
fun ListItemContainer(
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    isSelected: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                end = dimensionResource(R.dimen.margin_horizontal),
                bottom = 4.dp
            )
            .clip(
                RoundedCornerShape(
                    topStart = if (isFirst || isSelected) { 16.dp } else { 4.dp },
                    topEnd = if (isFirst || isSelected) { 16.dp } else { 4.dp },
                    bottomStart = if (isLast || isSelected) { 16.dp } else { 4.dp },
                    bottomEnd = if (isLast || isSelected) { 16.dp } else { 4.dp },
                )
            )
            .background(if (!isSelected) {
                containerColor
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(0.5f)
            })
    ) {
        content()
    }
}
