package de.passwordvault.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import de.passwordvault.R
import de.passwordvault.ui.theme.LocalPasswordVaultColors


/**
 * Displays a tab anc can be used within a tab row.
 *
 * @param title         Title of the tab.
 * @param index         Index of the tab.
 * @param selectedIndex Index of the tab that is currently selected.
 * @param onClick       Callback invoked once the tab is clicked.
 * @param modifier      Modifier.
 * @param badgeText     Optional text for a badge to display next to the title.
 */
@Composable
fun Tab(
    title: String,
    index: Int,
    selectedIndex: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null
) {
    androidx.compose.material3.Tab(
        selected = selectedIndex == index,
        selectedContentColor = MaterialTheme.colorScheme.primary,
        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
        onClick = {
            onClick(index)
        },
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    vertical = dimensionResource(R.dimen.padding_vertical),
                    horizontal = dimensionResource(R.dimen.padding_horizontal_between)
                )
        ) {
            Text(
                text = title,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            if (badgeText != null) {
                Badge(
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal_small))
                ) {
                    Text(
                        text = badgeText
                    )
                }
            }
        }
    }
}
