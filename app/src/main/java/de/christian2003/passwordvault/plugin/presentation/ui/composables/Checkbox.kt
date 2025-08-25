package de.christian2003.passwordvault.plugin.presentation.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.christian2003.passwordvault.R


/**
 * Checkbox component for the app.
 *
 * @param checked           Whether the checkbox is checked.
 * @param onCheckedChange   Callback invoked once the checked-state changes.
 * @param title             Title for the checkbox.
 * @param modifier          Modifier.
 * @param text              Optional subtext describing the checkbox in greater detail.
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    text: String? = null,
    onInfoClick: (() -> Unit)? = null
) {
    var checkboxStartPadding = dimensionResource(R.dimen.margin_horizontal) - 12.dp
    if (checkboxStartPadding < 0.dp) {
        checkboxStartPadding = 0.dp
    }
    var checkboxEndPadding = dimensionResource(R.dimen.padding_horizontal) - 12.dp
    if (checkboxEndPadding < 0.dp) {
        checkboxEndPadding = 0.dp
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onCheckedChange(!checked)
            }
            .padding(
                start = checkboxStartPadding,
                top = dimensionResource(R.dimen.padding_vertical),
                end = dimensionResource(R.dimen.margin_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = checkboxEndPadding)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
            if (text != null) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        if (onInfoClick != null) {
            IconButton(
                onClick = onInfoClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = ""
                )
            }
        }
    }
}
