package de.christian2003.accounts.view.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import de.christian2003.accounts.R
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.christian2003.core.ui.composables.TextInput


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountView(
    viewModel: AccountViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = viewModel.name.ifEmpty { stringResource(R.string.account_name_label) },
                        color = if (viewModel.name.isNotEmpty()) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) },
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                viewModel.isEditNameSheetVisible = !viewModel.isEditNameSheetVisible
                            }
                            .padding(
                                vertical = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_small),
                                horizontal = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_small)
                            )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.save()
                            onNavigateUp()
                        }
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.R.drawable.ic_back),
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        end = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity)
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity),
                            top = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_activity),
                            bottom = dimensionResource(de.christian2003.core.R.dimen.padding_vertical)
                        )
                        .size(dimensionResource(de.christian2003.core.R.dimen.image_large))
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Text(
                        text = if (viewModel.name.isNotEmpty()) { "" + viewModel.name[0] } else { "?" },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                }
                Text(
                    text = viewModel.description.ifEmpty { stringResource(R.string.account_description_label) },
                    color = if (viewModel.description.isNotEmpty()) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_activity) - dimensionResource(de.christian2003.core.R.dimen.padding_vertical_small),
                            start = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal) - dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_small)
                        )
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            viewModel.isEditDescriptionSheetVisible = !viewModel.isEditDescriptionSheetVisible
                        }
                        .padding(
                            vertical = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_small),
                            horizontal = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_small)
                        )
                )
            }
            HorizontalDivider()
            Text("Details")
        }
        if (viewModel.isEditNameSheetVisible) {
            ContentEditor(
                value = viewModel.name,
                onValueChange = {
                    viewModel.name = it
                },
                label = stringResource(R.string.account_name_label),
                info = stringResource(R.string.account_name_info),
                prefixIcon = painterResource(de.christian2003.core.R.drawable.ic_title),
                onDismiss = {
                    viewModel.isEditNameSheetVisible = false
                }
            )
        }
        if (viewModel.isEditDescriptionSheetVisible) {
            ContentEditor(
                value = viewModel.description,
                onValueChange = {
                    viewModel.description = it
                },
                label = stringResource(R.string.account_description_label),
                info = stringResource(R.string.account_description_info),
                prefixIcon = painterResource(de.christian2003.core.R.drawable.ic_description),
                onDismiss = {
                    viewModel.isEditDescriptionSheetVisible = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContentEditor(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    info: String,
    prefixIcon: Painter,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(de.christian2003.core.R.dimen.padding_horizontal_activity),
                    vertical = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_activity)
                )
        ) {
            Text(
                text = info,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.R.dimen.padding_vertical))
            )
            TextInput(
                value = value,
                onValueChange = onValueChange,
                label = label,
                prefixIcon = prefixIcon,
                errorMessage = if (value.isEmpty()) { stringResource(R.string.account_name_error) } else { null }
            )
            FlowRow(
                modifier = Modifier
                    .padding(top = dimensionResource(de.christian2003.core.R.dimen.padding_vertical_between))
                    .align(Alignment.End),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onDismiss()
                        }
                    },
                    enabled = value.isNotEmpty()
                ) {
                    Text(text = stringResource(R.string.account_button_ok))
                }
            }
        }
    }
}
