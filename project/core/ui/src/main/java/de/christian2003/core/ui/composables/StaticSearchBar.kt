package de.christian2003.core.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.R


/**
 * Static search bar that can be clicked.
 *
 * @param hint          Search hint displayed.
 * @param onClick       Callback invoked once the search bar is clicked.
 * @param modifier      Modifier.
 * @param prefixIcon    Prefix icon.
 */
@Composable
fun StaticSearchBar(
    hint: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    prefixIcon: Painter = painterResource(R.drawable.ic_search)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(SearchBarDefaults.colors().containerColor)
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            painter = prefixIcon,
            contentDescription = "",
            tint = SearchBarDefaults.inputFieldColors().unfocusedLabelColor,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
        )
        Text(
            text = hint,
            style = MaterialTheme.typography.bodyLarge,
            color = SearchBarDefaults.inputFieldColors().unfocusedLabelColor
        )
    }
}
