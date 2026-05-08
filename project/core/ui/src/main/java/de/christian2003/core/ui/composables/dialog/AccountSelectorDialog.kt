package de.christian2003.core.ui.composables.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.core.ui.model.AccountUiDto
import kotlin.uuid.Uuid
import de.christian2003.core.ui.R
import de.christian2003.core.ui.composables.EmptyPlaceholder
import de.christian2003.core.ui.composables.SearchField
import de.christian2003.core.ui.composables.Shape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.uuid.toKotlinUuid


/**
 * Dialog through which a list of accounts is displayed and the user can select accounts from this
 * list.
 *
 * @param title             Title for the dialog.
 * @param accounts          List of accounts to display.
 * @param selectedAccounts  IDs of the selected accounts.
 * @param onSave            Invoked once the dialog is dismissed and the selected accounts are to be
 *                          saved.
 * @param onDismiss         Invoked to dismiss the dialog without saving.
 * @param confirmButtonText Text for the button to save the dialog.
 * @param dismissButtonText Text for the button to dismiss the dialog.
 */
@Composable
fun AccountSelectorDialog(
    title: String,
    accounts: List<AccountUiDto>,
    selectedAccounts: Set<Uuid>,
    onSave: (Set<Uuid>) -> Unit,
    onDismiss: () -> Unit,
    confirmButtonText: String = stringResource(R.string.button_ok),
    dismissButtonText: String = stringResource(R.string.button_cancel)
) {
    val mutableSetSaver = Saver<MutableSet<Uuid>, List<String>>(
        save = { set -> set.map { it.toString() } },
        restore = { list -> list.map { UUID.fromString(it).toKotlinUuid() }.toMutableSet() }
    )

    val selectedAccountIds: MutableSet<Uuid> = rememberSaveable(saver = mutableSetSaver) {
        val mutableSet: MutableSet<Uuid> = mutableStateSetOf()
        mutableSet.addAll(selectedAccounts)
        return@rememberSaveable mutableSet
    }
    val filteredAccountIds: MutableSet<Uuid> = rememberSaveable(saver = mutableSetSaver) {
        val mutableSet: MutableSet<Uuid> = mutableStateSetOf()
        mutableSet.addAll(accounts.map { it.id })
        return@rememberSaveable mutableSet
    }

    val focusRequester: FocusRequester = remember { FocusRequester() }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                DialogHeader(
                    title = title,
                    focusRequester = focusRequester,
                    onSearch = { query ->
                        coroutineScope.launch(Dispatchers.Default) {
                            val filteredAccounts: MutableSet<Uuid> = mutableSetOf()
                            accounts.forEach { account ->
                                if (account.name.contains(query, true) || account.description.contains(query, true)) {
                                    filteredAccounts.add(account.id)
                                }
                            }
                            filteredAccountIds.clear()
                            filteredAccountIds.addAll(filteredAccounts)
                        }
                    }
                )
                HorizontalDivider()

                if (filteredAccountIds.isEmpty()) {
                    EmptyPlaceholder(
                        title = stringResource(R.string.dialog_searchAccounts_emptyPlaceholder_title),
                        subtitle = stringResource(R.string.dialog_searchAccounts_emptyPlaceholder_subtitle),
                        painter = painterResource(R.drawable.el_search),
                        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
                    )
                }
                else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        item {
                            SelectAllAccountsItem(
                                accountsSize = accounts.size,
                                selectAccountsSize = selectedAccountIds.size,
                                filteredAccountsSize = filteredAccountIds.size,
                                onSelectAllAccounts = {
                                    coroutineScope.launch(Dispatchers.Default) {
                                        if (selectedAccountIds.size == accounts.size) {
                                            selectedAccountIds.clear()
                                        }
                                        else {
                                            selectedAccountIds.addAll(accounts.map { it.id })
                                        }
                                    }
                                }
                            )
                        }
                        items(accounts) { account ->
                            if (filteredAccountIds.contains(account.id)) {
                                AccountListItem(
                                    account = account,
                                    isSelected = selectedAccountIds.contains(account.id),
                                    onSelectedChange = { isSelected ->
                                        if (isSelected) {
                                            selectedAccountIds.add(account.id)
                                        }
                                        else {
                                            selectedAccountIds.remove(account.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp
                        )
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(dismissButtonText)
                    }
                    TextButton(
                        onClick = {
                            onSave(selectedAccountIds)
                        },
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                    ) {
                        Text(confirmButtonText)
                    }
                }
            }
        }
    }
}


/**
 * List item through which to select all accounts.
 *
 * @param accountsSize          Total number of accounts.
 * @param selectAccountsSize    Number of selected accounts.
 * @param filteredAccountsSize  Number of filtered accounts.
 * @param onSelectAllAccounts   Invoked to select all accounts.
 */
@Composable
private fun SelectAllAccountsItem(
    accountsSize: Int,
    selectAccountsSize: Int,
    filteredAccountsSize: Int,
    onSelectAllAccounts: () -> Unit
) {
    AnimatedVisibility(filteredAccountsSize == accountsSize) {
        //All accounts are being displayed:
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp, // 24 - 12 = 12
                    top = dimensionResource(R.dimen.padding_vertical),
                    end = 12.dp, // 24 - 12 = 12
                    bottom = dimensionResource(R.dimen.padding_vertical)
                )
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable {
                    onSelectAllAccounts()
                }
                .padding(
                    start = dimensionResource(R.dimen.padding_horizontal),
                    top = dimensionResource(R.dimen.padding_vertical),
                    end = 0.dp,
                    bottom = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Text(
                text = stringResource(R.string.dialog_searchAccounts_selectAllLabel),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            //Checkbox:
            Checkbox(
                checked = selectAccountsSize == accountsSize,
                onCheckedChange = {
                    onSelectAllAccounts()
                }
            )
        }
    }
}


/**
 * Display a single account in a list.
 *
 * @param account           Account to display.
 * @param isSelected        Whether the account is selected.
 * @param onSelectedChange  Invoked once the account is (de)selected.
 */
@Composable
private fun AccountListItem(
    account: AccountUiDto,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    val invokeOnSelectedChange: (Boolean) -> Unit = { isSelectedNew ->
        if (isSelectedNew != isSelected) {
            onSelectedChange(isSelectedNew)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                invokeOnSelectedChange(!isSelected)
            }
            .padding(
                start = 24.dp,
                top = dimensionResource(R.dimen.padding_vertical),
                end = 12.dp, //24 - 12 = 12
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        //IMAGE
        if (account.icon == null) {
            val firstChar: Char? = account.name.firstOrNull { !it.isWhitespace() }
            Shape(
                shape = MaterialShapes.Clover8Leaf,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(dimensionResource(R.dimen.image_m))
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
                painter = rememberDrawablePainter(account.icon),
                contentDescription = "",
                modifier = Modifier.size(dimensionResource(R.dimen.image_m))
            )
        }

        //Name, Description:
        Column(
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.padding_horizontal))
                .weight(1f)
        ) {
            Text(
                text = account.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (account.description.isNotBlank()) {
                Text(
                    text = account.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        //Checkbox:
        Checkbox(
            checked = isSelected,
            onCheckedChange = invokeOnSelectedChange
        )
    }
}


/**
 * Header of the dialog either shows a simple title or a search input through which the user can
 * filter the accounts.
 *
 * @param title             Title for the dialog displayed when not showing the search input.
 * @param focusRequester    Focus requester used to request focus on the query input.
 * @param onSearch          Callback invoked to begin searching the accounts.
 * @param modifier          Modifier.
 */
@Composable
private fun DialogHeader(
    title: String,
    focusRequester: FocusRequester,
    onSearch: suspend (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery: String? by remember { mutableStateOf(null) }
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                bottom = 16.dp
            )
    ) {
        if (searchQuery == null) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
        else {
            val query: String? = searchQuery
            if (query != null) {
                SearchField(
                    query = query,
                    hint = stringResource(R.string.dialog_searchAccounts_queryHint),
                    focusRequester = focusRequester,
                    onQueryChange = { query ->
                        searchQuery = query
                    },
                    onSearch = {
                        coroutineScope.launch {
                            onSearch(query)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                LaunchedEffect(Unit) {
                    //Safe-call required, similar to all other focus requesters.
                    focusRequester?.requestFocus()
                }
            }
        }

        IconButton(
            onClick = {
                if (searchQuery == null) {
                    //Show query input:
                    searchQuery = ""
                }
                else {
                    //Start search:
                    coroutineScope.launch {
                        val query: String? = searchQuery
                        if (query != null) {
                            onSearch(query)
                        }
                    }
                }
            },
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_horizontal),
                end = 24.dp - 12.dp
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = ""
            )
        }
    }
}
