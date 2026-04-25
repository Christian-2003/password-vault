package de.christian2003.feature.search.presentation.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.SearchField
import de.christian2003.core.ui.composables.dialog.ConfirmDeleteDialog
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.feature.search.presentation.viewmodels.SearchViewModel
import kotlin.uuid.Uuid
import de.christian2003.feature.search.R
import de.christian2003.feature.search.domain.entities.SearchResult
import de.christian2003.feature.search.presentation.models.dialogs.SearchScreenDialog


/**
 * Screen through which the user can perform search operations within the app data.
 *
 * @param viewModel             View model.
 * @param onNavigateUp          Callback invoked to navigate up the navigation stack.
 * @param onNavigateToAccount   Callback invoked to navigate to the account with the specified ID.
 */
@Composable
internal fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToAccount: (Uuid) -> Unit
) {
    val allTags: List<Tag> by viewModel.allTags.collectAsState(emptyList())
    val focusRequester: FocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        //Require safe call since, focusRequester can be null during recomposition after rotating
        //the screen:
        focusRequester?.requestFocus()
    }

    Scaffold(
        topBar = {
            TopBar(
                query = viewModel.query,
                focusRequester = focusRequester,
                onQueryChange = {
                    viewModel.query = it
                },
                onNavigateUp = onNavigateUp,
                onSearch = {
                    viewModel.startSearch()
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            when {
                viewModel.isSearching -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LoadingIndicator()
                    }
                }
                viewModel.searchResult != null -> {
                    val searchResult: SearchResult? = viewModel.searchResult
                    if (searchResult != null) {
                        SearchResultView(
                            bottomPadding = bottomPadding,
                            searchResult = searchResult,
                            onAccountSelected = onNavigateToAccount,
                            onQueryIcon = {
                                viewModel.queryAccountIcon(it)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                else -> {
                    SearchFilterView(
                        bottomPadding = bottomPadding,
                        allTags = allTags,
                        selectedTags = viewModel.selectedTags,
                        recentQueries = viewModel.recentQueries,
                        editedTimeSpan = viewModel.editedTimeSpan,
                        createdTimeSpan = viewModel.createdTimeSpan,
                        onTagToggled = { tagId ->
                            viewModel.toggleTag(tagId)
                        },
                        onEditedTimeSpanSelected = {
                            viewModel.editedTimeSpan = it
                        },
                        onCreatedTimeSpanSelected = {
                            viewModel.createdTimeSpan = it
                        },
                        onRemoveRecentQueries = {
                            viewModel.dialog = SearchScreenDialog.ConfirmDeleteRecentQueries
                        },
                        onRecentQuerySelected = {
                            viewModel.query = it
                        },
                        onFormatDate = { date ->
                            viewModel.formatDate(date)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        NavigationBarProtection(bottomPadding)
    }

    when (viewModel.dialog) {
        SearchScreenDialog.ConfirmDeleteRecentQueries -> {
            ConfirmDeleteDialog(
                text = stringResource(R.string.search_filter_queriesDeleteDialogText),
                onConfirm = {
                    viewModel.removeRecentQueries()
                    viewModel.dialog = SearchScreenDialog.None
                },
                onDismiss = {
                    viewModel.dialog = SearchScreenDialog.None
                }
            )
        }
        else -> { }
    }
}


/**
 * Top bar for the screen.
 *
 * @param query             Search query.
 * @param focusRequester    Focus requester for the input field through which to enter the query.
 * @param onQueryChange     Callback invoked once the search query changes.
 * @param onNavigateUp      Callback invoked to navigate up the navigation stack.
 * @param onSearch          Callback invoked to start a search operation.
 */
@Composable
private fun TopBar(
    query: String,
    focusRequester: FocusRequester,
    onQueryChange: (String) -> Unit,
    onNavigateUp: () -> Unit,
    onSearch: () -> Unit
) {
    TopAppBar(
        title = {
            SearchField(
                query = query,
                hint = stringResource(R.string.search_hint),
                focusRequester = focusRequester,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateUp
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_back),
                    contentDescription = ""
                )
            }
        },
        actions = {
            IconButton(
                onClick = onSearch
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_search),
                    contentDescription = ""
                )
            }
        }
    )
}
