package de.christian2003.passwordvault.plugin.presentation.view.accounts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import kotlin.uuid.Uuid


@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToAccount: (id: Uuid) -> Unit
) {
    val allAccounts: List<Account> by viewModel.allAccounts.collectAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.entries_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (allAccounts.isEmpty()) {
                EmptyPlaceholder(
                    title = stringResource(R.string.entries_emptyPlaceholder_title),
                    subtitle = stringResource(R.string.entries_emptyPlaceholder_subtitle),
                    painter = painterResource(R.drawable.el_entries),
                    modifier = Modifier.fillMaxSize()
                )
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(allAccounts) { account ->
                        AccountsListRow(
                            account = account,
                            onEdit = {
                                onNavigateToAccount(it.descriptor.id)
                            },
                            onDelete = {
                                viewModel.deleteAccount(it)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun AccountsListRow(
    account: Account,
    onEdit: (Account) -> Unit,
    onDelete: (Account) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEdit(account)
            }
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = account.descriptor.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = account.descriptor.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = {
                onDelete(account)
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = ""
            )
        }
    }
}
