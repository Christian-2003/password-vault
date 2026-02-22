package de.christian2003.feature.search.presentation.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import de.christian2003.core.ui.composables.EmptyPlaceholder
import de.christian2003.feature.search.domain.entities.SearchResult
import de.christian2003.feature.search.R


@Composable
internal fun SearchResultView(
    bottomPadding: Dp,
    searchResult: SearchResult,
    modifier: Modifier = Modifier
) {
    if (searchResult.accountResults.isEmpty()) {
        //Empty placeholder:
        EmptyPlaceholder(
            title = stringResource(R.string.search_emptyPlaceholder_title),
            subtitle = stringResource(R.string.search_emptyPlaceholder_subtitle),
            painter = painterResource(R.drawable.el_search),
            modifier = modifier.padding(bottom = bottomPadding)
        )
    }
    else {
        //Search results:
        LazyColumn(
            modifier = modifier
        ) {
            itemsIndexed(searchResult.accountResults) { index, accountResult ->
                Text(text = accountResult.accountDescriptor.name)
            }

            item {
                Box(modifier = Modifier.height(bottomPadding))
            }
        }
    }
}
