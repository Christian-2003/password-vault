package de.passwordvault.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.drawable.toBitmap
import de.passwordvault.R
import de.passwordvault.model.entry.EntryAbbreviated
import de.passwordvault.ui.theme.LocalPasswordVaultColors


@Composable
fun EntryListRow(
    entry: EntryAbbreviated,
    onClick: ((EntryAbbreviated) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick != null) {
                onClick!!(entry)
            }
            .padding(
                horizontal = dimensionResource(R.dimen.space_horizontal),
                vertical = dimensionResource(R.dimen.space_vertical)
            )
    ) {
        //Logo of the app / or name abbreviation:
        if (entry.logo == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_m))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_xs)))
                    .background(LocalPasswordVaultColors.current.backgroundContainer)
            ) {
                Text(
                    text = if (entry.name.isNotEmpty()) { "" + entry.name[0] } else { "" },
                    color = LocalPasswordVaultColors.current.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        else {
            val bitmap: ImageBitmap = entry.logo.toBitmap().asImageBitmap()
            Icon(
                painter = BitmapPainter(bitmap),
                tint = Color.Unspecified,
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(R.dimen.image_m))
            )
        }

        //Name and description:
        Column(
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.space_horizontal_between))
        ) {
            Text(
                text = entry.name,
                color = LocalPasswordVaultColors.current.text,
                style = MaterialTheme.typography.bodyLarge
            )
            if (entry.description.isNotEmpty()) {
                Text(
                    text = entry.description,
                    color = LocalPasswordVaultColors.current.textVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
