package de.passwordvault.view.passwords.activity_analysis

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.passwordvault.R
import de.passwordvault.model.analysis.passwords.AnalyzedPassword
import de.passwordvault.model.entry.EntryAbbreviated
import de.passwordvault.ui.composables.EmptyPlaceholder
import de.passwordvault.ui.composables.EntryListRow
import de.passwordvault.ui.theme.LocalPasswordVaultColors
import de.passwordvault.view.utils.Utils


/**
 * Composable displays the tab which contains a list of all identical passwords.
 *
 * @param identicalPasswords    List of identical passwords.
 * @param onEntryClicked        Callback invoked once an entry is clicked.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IdenticalPasswordsTab(
    identicalPasswords: List<List<AnalyzedPassword>>,
    onEntryClicked: (EntryAbbreviated) -> Unit
) {
    if (identicalPasswords.isEmpty()) {
        EmptyPlaceholder(
            title = stringResource(R.string.password_all_empty_title),
            text = stringResource(R.string.password_all_empty_info),
            painter = painterResource(R.drawable.el_passwords),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        )
    }
    else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            identicalPasswords.forEach { group ->
                stickyHeader {
                    GroupHeader(
                        group = group
                    )
                }
                items(group) { password ->
                    EntryListRow(
                        entry = password.entry,
                        onClick = onEntryClicked
                    )
                }
            }
        }
    }
}


/**
 * Composable displays a sticky group header.
 *
 * @param group Group for which to display the sticky header.
 */
@Composable
private fun GroupHeader(
    group: List<AnalyzedPassword>
) {
    var obfuscated: Boolean by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.padding_horizontal),
                    top = dimensionResource(R.dimen.padding_vertical),
                    end = dimensionResource(R.dimen.padding_horizontal_end_button),
                    bottom = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (obfuscated) { Utils.obfuscate(group[0].password) } else { group[0].password },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.password_results_duplicates_number).replace("{arg}", "" + group.size),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(
                onClick = {
                    obfuscated = !obfuscated
                }
            ) {
                Icon(
                    painter = if (obfuscated) { painterResource(R.drawable.ic_show) } else { painterResource(R.drawable.ic_show_off) },
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = ""
                )
            }
        }
    }
}
