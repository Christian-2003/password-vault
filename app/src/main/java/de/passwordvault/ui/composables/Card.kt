package de.passwordvault.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import de.passwordvault.R
import de.passwordvault.ui.theme.LocalPasswordVaultColors



@Composable
fun Card(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    foregroundColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    icon: Painter = painterResource(R.drawable.ic_info_filled)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_l)))
            .background(backgroundColor)
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
    ) {
        Icon(
            painter = icon,
            tint = foregroundColor,
            contentDescription = "",
            modifier = Modifier.padding(end = dimensionResource(R.dimen.space_horizontal_between))
        )
        Text(
            text = text,
            color = foregroundColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
