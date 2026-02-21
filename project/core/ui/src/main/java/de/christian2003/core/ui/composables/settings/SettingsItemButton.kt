package de.christian2003.core.ui.composables.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import de.christian2003.core.ui.composables.ListItemContainer


/**
 * Composable displays an item button for the settings.
 *
 * @param title         Title for the setting.
 * @param info          Info for the setting.
 * @param onClick       Callback to invoke when the item button is clicked.
 * @param endIcon       Optional end icon.
 * @param isFirst       Whether this is the first list item.
 * @param isLast        Whether this is the last list item.
 * @param isEnabled     Whether the button is enabled.
 * @param prefixIcon    Optional prefix icon.
 * @param badgeCount    Optional count shown in a badge.
 */
@Composable
fun SettingsItemButton(
    title: String,
    info: String,
    onClick: () -> Unit,
    endIcon: Painter? = null,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    isEnabled: Boolean = true,
    prefixIcon: Painter? = null,
    badgeCount: Int = 0
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(isEnabled) {
                    onClick()
                }
                .padding(
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (prefixIcon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                        .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                ) {
                    Icon(
                        painter = prefixIcon,
                        tint = if (isEnabled) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
                        },
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                    if (badgeCount > 0) {
                        Badge(
                            modifier = Modifier.offset(
                                x = dimensionResource(de.christian2003.core.ui.R.dimen.image_xs) / 3,
                                y = dimensionResource(de.christian2003.core.ui.R.dimen.image_xs) / -3
                            )
                        ) {
                            Text(badgeCount.toString())
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = if (isEnabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (endIcon != null) {
                        Icon(
                            painter = endIcon,
                            tint = if (isEnabled) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(0.5f)
                            },
                            contentDescription = "",
                            modifier = Modifier
                                .padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) / 2)
                                .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxs))
                        )
                    }
                }
                Text(
                    text = info,
                    color = if (isEnabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
