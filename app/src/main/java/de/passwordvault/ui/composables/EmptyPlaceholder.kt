package de.passwordvault.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import de.passwordvault.ui.theme.LocalPasswordVaultColors
import de.passwordvault.R


/**
 * Composable displays an empty placeholder.
 *
 * @param title     Title for the empty placeholder.
 * @param text      Text for the empty placeholder.
 * @param painter   Painter for the icon of the empty placeholder.
 * @param modifier  Modifier.
 */
@Composable
fun EmptyPlaceholder(
    title: String,
    text: String,
    painter: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            painter = painter,
            tint = Color.Unspecified,
            contentDescription = "",
            modifier = Modifier.size(dimensionResource(R.dimen.image_xxl))
        )
        Text(
            text = title,
            color = LocalPasswordVaultColors.current.text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = text,
            color = LocalPasswordVaultColors.current.textVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
