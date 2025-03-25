package de.passwordvault.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import de.passwordvault.R


/**
 * Composable displays a generic text button.
 *
 * @param title         Title for the button.
 * @param description   Description for the button.
 * @param modifier      Modifier.
 * @param prefixIcon    Prefix icon for the button.
 * @param suffixIcon    Suffix icon for the button.
 * @param onClick       Callback invoked once the button is clicked.
 */
@Composable
fun GenericTextButton(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    prefixIcon: Painter? = null,
    suffixIcon: Painter? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick != null) {
                if (onClick != null) {
                    onClick()
                }
            }
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
    ) {
        if (prefixIcon != null) {
            Icon(
                painter = prefixIcon,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.space_horizontal_between))
                    .size(dimensionResource(R.dimen.image_xs))
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (suffixIcon != null) {
                    Icon(
                        painter = suffixIcon,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = dimensionResource(R.dimen.space_horizontal_small))
                            .size(dimensionResource(R.dimen.image_xxs))
                    )
                }
            }
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
