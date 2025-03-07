package de.passwordvault.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import de.passwordvault.ui.theme.LocalPasswordVaultColors
import de.passwordvault.R


/**
 * Composable displays a generic text button.
 *
 * @param title         Title for the button.
 * @param description   Description for the button.
 * @param modifier      Modifier.
 * @param onClick       Callback invoked once the button is clicked.
 */
@Composable
fun GenericTextButton(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick != null) {
                if (onClick != null) {
                    onClick()
                }
            }
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical_between)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = LocalPasswordVaultColors.current.text,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                color = LocalPasswordVaultColors.current.textVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
