package de.christian2003.core.ui.composables.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import de.christian2003.core.ui.composables.ListItemContainer


/**
 * Composable displays an item switch for the settings.
 *
 * @param title             Title for the setting.
 * @param info              Info for the setting.
 * @param checked           Whether the switch is checked.
 * @param onCheckedChange   Callback invoked once the switch is (un)checked.
 * @param prefixIcon        Optional prefix icon.
 */
@Composable
fun SettingsItemSwitch(
    title: String,
    info: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    isEnabled: Boolean = true,
    prefixIcon: Painter? = null
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(isEnabled) {
                    onCheckedChange(!checked)
                }
                .padding(
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (prefixIcon != null) {
                Icon(
                    painter = prefixIcon,
                    tint = if (isEnabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
                    },
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                        .size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Column(
                modifier = Modifier.weight(1f)
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
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = isEnabled,
                modifier = Modifier.padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
            )
        }
    }
}
