package de.passwordvault.ui.composables

import android.graphics.Paint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import de.passwordvault.R
import de.passwordvault.ui.theme.LocalPasswordVaultColors


/**
 * Displays a headline.
 *
 * @param title     Title of the headline.
 * @param endIcon   End icon for the headline.
 * @param onClick   Optional callback invoked once the headline is clicked.
 */
@Composable
fun Headline(
    title: String,
    modifier: Modifier = Modifier,
    endIcon: Painter? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick != null) {
                onClick!!()
            }
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
    ) {
        Text(
            text = title,
            color = LocalPasswordVaultColors.current.textVariant,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        if (endIcon != null) {
            Icon(
                painter = endIcon,
                tint = LocalPasswordVaultColors.current.textVariant,
                contentDescription = "",
                modifier = Modifier.padding(start = dimensionResource(R.dimen.space_horizontal_between))
            )
        }
    }
}
