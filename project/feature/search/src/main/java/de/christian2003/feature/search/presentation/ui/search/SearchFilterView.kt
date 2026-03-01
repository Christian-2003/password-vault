package de.christian2003.feature.search.presentation.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.Tooltip
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.feature.search.R
import kotlin.uuid.Uuid


@Composable
internal fun SearchFilterView(
    bottomPadding: Dp,
    allTags: List<Tag>,
    selectedTags: Set<Uuid>,
    recentQueries: List<String>,
    onTagToggled: (Uuid) -> Unit,
    onRemoveRecentQueries: () -> Unit,
    onRecentQuerySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        Headline(
            title = stringResource(R.string.search_filter_title),
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )

        //Tags:
        CategoryLabel(
            label = stringResource(R.string.search_filter_tagsLabel),
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )
        TagsSelector(
            allTags = allTags,
            selectedTags = selectedTags,
            onTagToggled = onTagToggled,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )

        //Recent queries:
        Headline(
            title = stringResource(R.string.search_filter_queriesTitle),
            endIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
            tooltip = stringResource(R.string.search_filter_queriesDeleteTooltip),
            onClick = {
                onRemoveRecentQueries()
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical) * 2)
                .clip(RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                ))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        )
        RecentQueriesList(
            recentQueries = recentQueries,
            onRecentQuerySelected = onRecentQuerySelected
        )

        //Bottom padding:
        Box(modifier = Modifier.height(bottomPadding))
    }
}


@Composable
private fun TagsSelector(
    allTags: List<Tag>,
    selectedTags: Set<Uuid>,
    onTagToggled: (Uuid) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndices: MutableList<Int> = mutableListOf()
    allTags.forEachIndexed { index, tag ->
        if (selectedTags.contains(tag.id)) {
            selectedIndices.add(index)
        }
    }

    ChipRow(
        chipLabels = allTags.map { it.name },
        selectedIndices = selectedIndices,
        onSelectionChanged = { index ->
            onTagToggled(allTags[index].id)
        },
        modifier = modifier
    )
}


@Composable
private fun RecentQueriesList(
    recentQueries: List<String>,
    onRecentQuerySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (recentQueries.isEmpty()) {
            Text(
                text = stringResource(R.string.search_filter_queriesEmptyLabel),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                )
            )
        }
        else {
            recentQueries.forEachIndexed { index, query ->
                ListItemContainer(
                    isFirst = index == 0,
                    isLast = index == recentQueries.size - 1
                ) {
                    Text(
                        text = query,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onRecentQuerySelected(query)
                            }
                            .padding(
                                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                            )
                    )
                }
            }
        }
    }
}


@Composable
private fun CategoryLabel(
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
            )
    )
}


@Composable
private fun ChipRow(
    chipLabels: List<String>,
    selectedIndices: List<Int>,
    onSelectionChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            //Start margin
            Box(modifier = Modifier.width(dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)))
        }
        itemsIndexed(chipLabels) { index, label ->
            FilterChip(
                selected = selectedIndices.contains(index),
                onClick = {
                    onSelectionChanged(index)
                },
                label = {
                    Text(label)
                },
                modifier = Modifier.padding(
                    start = if (index != 0) { dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) } else { 0.dp }
                )
            )
        }
        item {
            //End margin
            Box(modifier = Modifier.width(dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)))
        }
    }
}
