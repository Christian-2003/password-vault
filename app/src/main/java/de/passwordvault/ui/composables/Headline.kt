package de.passwordvault.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import de.passwordvault.R
import de.passwordvault.ui.theme.LocalPasswordVaultColors


/**
 * Displays a headline.
 *
 * @param title Title of the headline.
 */
@Composable
fun Headline(
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
    ) {
        Text(
            text = title,
            color = LocalPasswordVaultColors.current.textVariant,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
