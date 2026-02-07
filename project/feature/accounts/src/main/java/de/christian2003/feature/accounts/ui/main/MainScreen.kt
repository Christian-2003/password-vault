package de.christian2003.feature.accounts.ui.main

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.core.ui.composables.EmptyPlaceholder
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.Shape
import de.christian2003.core.ui.composables.dialog.ConfirmDeleteDialog
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.feature.accounts.viewmodels.MainViewModel
import kotlin.uuid.Uuid
import de.christian2003.feature.accounts.R


/**
 * Displays the main screen.
 *
 * @param viewModel             View model.
 * @param onEditAccount         Callback invoked to edit an account. The account ID is passed as argument.
 * @param onCreateNewAccount    Callback invoked to create a new account.
 * @param onNavigateToSettings  Callback invoked to navigate to the settings.
 */
@Composable
internal fun MainScreen(
    viewModel: MainViewModel,
    onEditAccount: (Uuid) -> Unit,
    onCreateNewAccount: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val accountDescriptors: List<AccountDescriptor> by viewModel.accountDescriptors.collectAsState(emptyList())
    val appBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(de.christian2003.core.ui.R.string.app_name))
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_settings),
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNewAccount,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_add),
                    contentDescription = ""
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .fillMaxSize()
        ) {
            AccountDescriptorsList(
                accountDescriptors = accountDescriptors,
                onEditAccount = { accountDescriptor ->
                    onEditAccount(accountDescriptor.id)
                },
                onDeleteAccount = { accountDescriptor ->
                    viewModel.accountToDelete = accountDescriptor
                },
                onQueryIcon = { account ->
                    viewModel.queryIconForAccount(account)
                },
                windowInsets = WindowInsets(bottom = innerPadding.calculateBottomPadding())
            )
        }
        if (accountDescriptors.isNotEmpty()) {
            NavigationBarProtection(innerPadding.calculateBottomPadding())
        }

        val accountToDelete: AccountDescriptor? = viewModel.accountToDelete
        if (accountToDelete != null) {
            ConfirmDeleteDialog(
                text = stringResource(R.string.main_deleteAccountText, accountToDelete.name),
                onConfirm = {
                    viewModel.deleteAccount()
                },
                onDismiss = {
                    viewModel.accountToDelete = null
                }
            )
        }
    }
}


/**
 * Displays a list of account descriptors.
 *
 * @param accountDescriptors    List of descriptors to display.
 * @param onEditAccount         Callback invoked to edit an account.
 * @param onDeleteAccount       Callback invoked to delete an account.
 * @param onQueryIcon       Callback invoked to query the account icon.
 * @param windowInsets          Window insets with the bottom padding.
 */
@Composable
private fun AccountDescriptorsList(
    accountDescriptors: List<AccountDescriptor>,
    onEditAccount: (AccountDescriptor) -> Unit,
    onDeleteAccount: (AccountDescriptor) -> Unit,
    onQueryIcon: (AccountDescriptor) -> Drawable?,
    windowInsets: WindowInsets
) {
    if (accountDescriptors.isEmpty()) {
        EmptyPlaceholder(
            painter = painterResource(R.drawable.el_accounts),
            title = stringResource(R.string.main_emptyPlaceholder_title),
            subtitle = stringResource(R.string.main_emptyPlaceholder_subtitle),
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(windowInsets)
        )
    }
    else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(accountDescriptors) { index, accountDescriptor ->
                AccountDescriptorsListItem(
                    accountDescriptor = accountDescriptor,
                    isFirst = index == 0,
                    isLast = index == accountDescriptors.size - 1,
                    onEdit = onEditAccount,
                    onDelete = onDeleteAccount,
                    onQueryIcon = onQueryIcon
                )
            }
            //Last item has the same height as the bottom navigation bar. This makes it possible to
            //scroll the last account descriptor past the navigation bar. Therefore, the last descriptor
            //is not displayed below the navigation bar which is touch protected.
            item {
                Box(
                    modifier = Modifier.windowInsetsBottomHeight(windowInsets)
                )
            }
        }
    }
}


/**
 * Displays a list item for an account descriptor.
 *
 * @param accountDescriptor Descriptor for which to display the list item.
 * @param isFirst           Whether the descriptor is the first in the list.
 * @param isLast            Whether the descriptor is the last in the list.
 * @param onEdit            Callback invoked to edit the account.
 * @param onDelete          Callback invoked to delete the account.
 * @param onQueryIcon       Callback invoked to query the account icon.
 */
@Composable
private fun AccountDescriptorsListItem(
    accountDescriptor: AccountDescriptor,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: (AccountDescriptor) -> Unit,
    onDelete: (AccountDescriptor) -> Unit,
    onQueryIcon: (AccountDescriptor) -> Drawable?
) {
    var isDropdownVisible: Boolean by remember { mutableStateOf(false) }

    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onEdit(accountDescriptor)
                }
                .padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) - 12.dp,
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            //Icon:
            val icon: Drawable? = onQueryIcon(accountDescriptor)
            if (icon == null) {
                val firstChar: Char? = accountDescriptor.name.firstOrNull { !it.isWhitespace() }
                Shape(
                    shape = MaterialShapes.Clover8Leaf,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
                ) {
                    Text(
                        text = firstChar?.toString() ?: "",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            else {
                Image(
                    painter = rememberDrawablePainter(icon),
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
                )
            }

            //Name and description:
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
            ) {
                Text(
                    text = accountDescriptor.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (accountDescriptor.description.isNotBlank()) {
                    Text(
                        text = accountDescriptor.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            //Dropdown menu:
            IconButton(
                onClick = {
                    isDropdownVisible = !isDropdownVisible
                }
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_more),
                    contentDescription = ""
                )
                DropdownMenu(
                    expanded = isDropdownVisible,
                    onDismissRequest = {
                        isDropdownVisible = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.main_accounts_edit))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_edit),
                                contentDescription = ""
                            )
                        },
                        onClick = {
                            isDropdownVisible = false
                            onEdit(accountDescriptor)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.main_accounts_delete))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
                                contentDescription = ""
                            )
                        },
                        onClick = {
                            isDropdownVisible = false
                            onDelete(accountDescriptor)
                        }
                    )
                }
            }
        }
    }
}
