package de.christian2003.feature.files.ui.breadcrumbs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource


@Composable
fun Breadcrumbs(
    items: List<Breadcrumb>,
    modifier: Modifier = Modifier
) {
    val lazyListState: LazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        lazyListState.scrollToItem(items.lastIndex)
    }

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        state = lazyListState,
        modifier = modifier
    ) {
        itemsIndexed(items) { index, breadcrumb ->
            BreadcrumbsItem(
                breadcrumb = breadcrumb,
                isHighlighted = index == items.size - 1
            )
            if (index < items.size - 1) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_next),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xxs))
                )
            }
        }
    }
}


@Composable
private fun BreadcrumbsItem(
    breadcrumb: Breadcrumb,
    isHighlighted: Boolean
) {
    val color: Color = if (isHighlighted) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(breadcrumb.onClick != null) {
                if (breadcrumb.onClick != null) {
                    breadcrumb.onClick()
                }
            }
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        if (breadcrumb.leadingIcon != null) {
            Icon(
                painter = breadcrumb.leadingIcon,
                contentDescription = "",
                tint = color
            )
        }
        Text(
            text = breadcrumb.label,
            color = color,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
