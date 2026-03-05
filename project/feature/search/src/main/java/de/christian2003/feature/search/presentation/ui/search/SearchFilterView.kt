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
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.feature.search.R
import de.christian2003.feature.search.presentation.models.other.FilterTimeSpan
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.uuid.Uuid


@Composable
internal fun SearchFilterView(
    bottomPadding: Dp,
    allTags: List<Tag>,
    selectedTags: Set<Uuid>,
    editedTimeSpan: FilterTimeSpan,
    createdTimeSpan: FilterTimeSpan,
    recentQueries: List<String>,
    onTagToggled: (Uuid) -> Unit,
    onEditedTimeSpanSelected: (FilterTimeSpan) -> Unit,
    onCreatedTimeSpanSelected: (FilterTimeSpan) -> Unit,
    onRemoveRecentQueries: () -> Unit,
    onRecentQuerySelected: (String) -> Unit,
    onFormatDate: (LocalDate) -> String,
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
        if (allTags.isNotEmpty()) {
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
        }

        //Edited:
        CategoryLabel(
            label = stringResource(R.string.search_filter_editedLabel),
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )
        FilterTimeSpanSelector(
            selectedTimeSpan = editedTimeSpan,
            onSelectionChanged = onEditedTimeSpanSelected,
            onFormatDate = onFormatDate,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )

        //Created:
        CategoryLabel(
            label = stringResource(R.string.search_filter_createdLabel),
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )
        FilterTimeSpanSelector(
            selectedTimeSpan = createdTimeSpan,
            onSelectionChanged = onCreatedTimeSpanSelected,
            onFormatDate = onFormatDate,
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
private fun FilterTimeSpanSelector(
    selectedTimeSpan: FilterTimeSpan,
    onSelectionChanged: (FilterTimeSpan) -> Unit,
    onFormatDate: (LocalDate) -> String,
    modifier: Modifier = Modifier
) {
    var customTimeSpan: FilterTimeSpan? by rememberSaveable { mutableStateOf(null) }

    val selectedIndex: Int = when(selectedTimeSpan) {
        FilterTimeSpan.All -> 0
        FilterTimeSpan.Today -> 1
        FilterTimeSpan.LastWeek -> 2
        else -> 3
    }

    val labels: List<String> = listOf(
        stringResource(R.string.search_filter_timeSpan_all),
        stringResource(R.string.search_filter_timeSpan_today),
        stringResource(R.string.search_filter_timeSpan_lastWeek),
        if (selectedIndex != 3) {
            stringResource(R.string.search_filter_timeSpan_custom)
        } else {
            val datesCount: Int = if (selectedTimeSpan.start == selectedTimeSpan.end) { 1 } else { 2 }
            pluralStringResource(R.plurals.search_filter_timeSpan_customWithDates, datesCount, onFormatDate(selectedTimeSpan.start!!), onFormatDate(selectedTimeSpan.end!!))
        }
    )

    ChipRow(
        chipLabels = labels,
        selectedIndices = listOf(selectedIndex),
        onSelectionChanged = { index ->
            when (index) {
                0 -> onSelectionChanged(FilterTimeSpan.All)
                1 -> onSelectionChanged(FilterTimeSpan.Today)
                2 -> onSelectionChanged(FilterTimeSpan.LastWeek)
                3 -> customTimeSpan = selectedTimeSpan
            }
        },
        modifier = modifier
    )

    val customTimeSpanImmutable: FilterTimeSpan? = customTimeSpan
    if (customTimeSpanImmutable != null) {
        DateRangePickerModal(
            filterTimeSpan = customTimeSpanImmutable,
            onTimeSpanSelected = {
                customTimeSpan = null
                onSelectionChanged(it)
            },
            onDismiss = {
                customTimeSpan = null
            },
            onFormatDate = onFormatDate
        )
    }
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


/**
 * Displays a model date picker through which to select a date range for search filters.
 *
 * @param filterTimeSpan        Time span that is selected currently.
 * @param onTimeSpanSelected    Callback invoked once a new time span is selected.
 * @param onDismiss             Callback invoked to close the dialog without selecting a new
 *                              time span.
 * @param onFormatDate          Callback invoked to format a date.
 */
@Composable
private fun DateRangePickerModal(
    filterTimeSpan: FilterTimeSpan,
    onTimeSpanSelected: (FilterTimeSpan) -> Unit,
    onDismiss: () -> Unit,
    onFormatDate: (LocalDate) -> String
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDate = filterTimeSpan.start,
        initialSelectedEndDate = filterTimeSpan.end
    )

    val dateFormatter: DatePickerFormatter = object: DatePickerFormatter {
        override fun formatMonthYear(monthMillis: Long?, locale: CalendarLocale): String? {
            return ""
        }

        override fun formatDate(dateMillis: Long?, locale: CalendarLocale, forContentDescription: Boolean): String? {
            val date: LocalDate = if (dateMillis != null) {
                LocalDate.ofInstant(Instant.ofEpochMilli(dateMillis), ZoneOffset.UTC)
            } else {
                LocalDate.now()
            }
            return onFormatDate(date)
        }

    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val start: LocalDate? = dateRangePickerState.getSelectedStartDate() ?: filterTimeSpan.start
                    val end: LocalDate? = dateRangePickerState.getSelectedEndDate() ?: filterTimeSpan.end
                    if (start != null && end != null) {
                        val selectedTimeSpan = if (start.isBefore(end)) {
                            FilterTimeSpan(start, end)
                        } else {
                            FilterTimeSpan(end, start)
                        }
                        onTimeSpanSelected(selectedTimeSpan)
                    }
                }
            ) {
                Text(stringResource(de.christian2003.core.ui.R.string.button_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(de.christian2003.core.ui.R.string.button_cancel))
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                DateRangePickerDefaults.DateRangePickerTitle(
                    displayMode = dateRangePickerState.displayMode,
                    modifier = Modifier
                        .weight(1f)
                        .padding( //Library has incorrect padding. So we need to override here manually!
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp
                        )
                )
            },
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                    selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                    displayMode = dateRangePickerState.displayMode,
                    dateFormatter = dateFormatter,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp) //Library has incorrect padding. So we need to override here manually!
                )
            }
        )
    }
}
