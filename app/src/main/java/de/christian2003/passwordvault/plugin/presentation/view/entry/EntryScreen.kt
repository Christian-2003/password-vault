package de.christian2003.passwordvault.plugin.presentation.view.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.TextInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.text.ifEmpty


@Composable
fun EntryScreen(
    viewModel: EntryViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewModel.name.ifEmpty { stringResource(R.string.entry_namePlaceholder) },
                        color = if (!viewModel.name.isEmpty()) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        },
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                viewModel.isNameDialogVisible = true
                            }
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            GeneralSection(
                description = viewModel.description,
                onEditDescription = {
                    viewModel.isDescriptionDialogVisible = true
                },
                name = viewModel.name,
                onSave = {
                    viewModel.save()
                    onNavigateUp()
                },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
            )
        }
        if (viewModel.isNameDialogVisible) {
            EditDataDialog(
                value = viewModel.name,
                label = stringResource(R.string.entry_nameLabel),
                infoText = stringResource(R.string.entry_nameInfo),
                prefixIcon = painterResource(R.drawable.ic_name),
                onDismiss = {
                    viewModel.isNameDialogVisible = false
                },
                onSave = { name ->
                    viewModel.name = name
                    viewModel.isNameDialogVisible = false
                }
            )
        }
        if (viewModel.isDescriptionDialogVisible) {
            EditDataDialog(
                value = viewModel.description,
                label = stringResource(R.string.entry_descriptionLabel),
                infoText = stringResource(R.string.entry_descriptionInfo),
                prefixIcon = painterResource(R.drawable.ic_description),
                onDismiss = {
                    viewModel.isDescriptionDialogVisible = false
                },
                onSave = { description ->
                    viewModel.description = description
                    viewModel.isDescriptionDialogVisible = false
                }
            )
        }
    }
}


@Composable
private fun GeneralSection(
    description: String,
    onEditDescription: () -> Unit,
    name: String,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .padding(
                start = dimensionResource(R.dimen.padding_horizontal),
                end = dimensionResource(R.dimen.padding_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Row {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.padding_vertical))
                    .size(dimensionResource(R.dimen.image_xl))
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Text(
                    text = if (!name.isEmpty()) { name.first().toString() } else { "?" },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLargeEmphasized,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = description.ifEmpty { stringResource(R.string.entry_descriptionPlaceholder) },
                    color = if (!description.isEmpty()) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(0.5f) },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(R.dimen.padding_horizontal) - 8.dp,
                            top = dimensionResource(R.dimen.padding_vertical) - 4.dp
                        )
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            onEditDescription()
                        }
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        )
                )
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .padding(start = dimensionResource(R.dimen.padding_horizontal))
                ) {
                    Text(stringResource(R.string.button_save))
                }
            }
        }
        //Add tags here...
    }
}


@Composable
private fun EditDataDialog(
    value: String,
    label: String,
    infoText: String,
    prefixIcon: Painter,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val focusRequester: FocusRequester = remember { FocusRequester() }
    var mutableValue: String by remember { mutableStateOf(value) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.margin_horizontal),
                    end = dimensionResource(R.dimen.margin_horizontal),
                    bottom = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Text(
                text = infoText,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            TextInput(
                value = mutableValue,
                onValueChange = {
                    mutableValue = it
                },
                label = label,
                prefixIcon = prefixIcon,
                focusRequester = focusRequester,
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
            )
            FlowRow(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.align(Alignment.End)
            ) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onDismiss()
                        }
                    }
                ) {
                    Text(stringResource(R.string.button_cancel))
                }
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onSave(mutableValue)
                        }
                    },
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                ) {
                    Text(stringResource(R.string.button_save))
                }
            }
        }
    }
}
