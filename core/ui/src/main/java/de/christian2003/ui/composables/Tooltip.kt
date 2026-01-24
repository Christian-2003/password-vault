package de.christian2003.ui.composables

import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable


/**
 * Composable can be wrapped around another composable for which to display a tooltip.
 *
 * @param tooltip   Tooltip text to display.
 * @param anchor    Optional tooltip anchor position.
 * @param content   Content for which to display the tooltip.
 */
@Composable
fun Tooltip(
    tooltip: String,
    anchor: TooltipAnchorPosition = TooltipAnchorPosition.End,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = anchor
        ),
        tooltip = {
            RichTooltip {
                Text(tooltip)
            }
        },
        state = rememberTooltipState()
    ) {
        content()
    }
}
