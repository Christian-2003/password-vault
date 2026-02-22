package de.christian2003.feature.search.presentation.ui.search

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.core.ui.composables.EmptyPlaceholder
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.Shape
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.feature.search.domain.entities.SearchResult
import de.christian2003.feature.search.R
import de.christian2003.feature.search.domain.entities.AccountSearchResult
import kotlin.uuid.Uuid


@Composable
internal fun SearchResultView(
    bottomPadding: Dp,
    searchResult: SearchResult,
    modifier: Modifier = Modifier,
    onAccountSelected: (Uuid) -> Unit,
    onQueryIcon: (AccountDescriptor) -> Drawable?
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
                AccountSearchResultItem(
                    accountSearchResult = accountResult,
                    isFirst = index == 0,
                    isLast = index == searchResult.accountResults.size - 1,
                    onQueryIcon = onQueryIcon,
                    onAccountSelected = onAccountSelected
                )
            }

            item {
                Box(modifier = Modifier.height(bottomPadding))
            }
        }
    }
}



@Composable
private fun AccountSearchResultItem(
    accountSearchResult: AccountSearchResult,
    isFirst: Boolean,
    isLast: Boolean,
    onQueryIcon: (AccountDescriptor) -> Drawable?,
    onAccountSelected: (Uuid) -> Unit
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onAccountSelected(accountSearchResult.accountDescriptor.id)
                }
                .padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) - 12.dp,
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            //Icon:
            val icon: Drawable? = onQueryIcon(accountSearchResult.accountDescriptor)
            if (icon == null) {
                val firstChar: Char? = accountSearchResult.accountDescriptor.name.firstOrNull { !it.isWhitespace() }
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
            } else {
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
                    text = accountSearchResult.accountDescriptor.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (accountSearchResult.accountDescriptor.description.isNotBlank()) {
                    Text(
                        text = accountSearchResult.accountDescriptor.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
