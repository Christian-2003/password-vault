package de.christian2003.passwordvault.plugin.presentation.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import de.christian2003.passwordvault.R


/**
 * Displays an empty placeholder to indicate that no data is available to be displayed.
 * The empty placeholder can display a button beneath the subtitle, if "onButtonClick" and
 * "buttonContent" are not null.
 *
 * @param title         Title for the empty placeholder.
 * @param subtitle      Subtitle for the empty placeholder.
 * @param painter       Painter for the empty placeholder.
 * @param modifier      Modifier.
 * @param onButtonClick Listener to invoke once the button beneath the empty placeholder is clicked.
 * @param buttonContent Content for the button beneath the empty placeholder.
 */
@Composable
fun EmptyPlaceholder(
    title: String,
    subtitle: String,
    painter: Painter,
    modifier: Modifier = Modifier,
    onButtonClick: (() -> Unit)? = null,
    buttonContent: (@Composable () -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painter,
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(R.dimen.image_emptyPlaceholder))
            )
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.padding_vertical))
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if (buttonContent != null && onButtonClick != null) {
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
                ) {
                    buttonContent()
                }
            }
        }
    }
}
