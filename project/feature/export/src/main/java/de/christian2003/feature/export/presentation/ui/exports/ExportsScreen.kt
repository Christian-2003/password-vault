package de.christian2003.feature.export.presentation.ui.exports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.feature.export.domain.entities.ExportDescriptor
import de.christian2003.feature.export.presentation.viewmodels.ExportsViewModel
import de.christian2003.feature.export.R


@Composable
internal fun ExportsScreen(
    viewModel: ExportsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToExport: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            itemsIndexed(viewModel.exportDescriptors) { index, descriptor ->
                ExportDescriptorListRow(
                    descriptor = descriptor,
                    isFirst = index == 0,
                    isLast = index == viewModel.exportDescriptors.size - 1,
                    onClick = onNavigateToExport
                )
            }

            item {
                Box(modifier = Modifier.height(bottomPadding))
            }
        }

        NavigationBarProtection(bottomPadding)
    }
}


@Composable
private fun ExportDescriptorListRow(
    descriptor: ExportDescriptor,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: (String) -> Unit
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(descriptor.id)
                }
                .padding(
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            Text(
                text = stringResource(descriptor.titleId),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(descriptor.subtitleId),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
private fun TopBar(
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.exports_title))
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
        }
    )
}
