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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.domain.entry.Detail
import de.christian2003.passwordvault.domain.entry.Tag
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Headline
import de.christian2003.passwordvault.plugin.presentation.ui.composables.TextInput
import de.christian2003.passwordvault.plugin.presentation.view.tag.TagSheet
import de.christian2003.passwordvault.plugin.presentation.view.tag.TagViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.text.ifEmpty
import kotlin.uuid.Uuid


@Composable
fun EntryScreen(
    viewModel: EntryViewModel,
    onNavigateUp: () -> Unit,
    onEditDetail: (Uuid) -> Unit,
    onCreateDetail: (Uuid) -> Unit
) {
    val details: List<Detail> by viewModel.details.collectAsState(emptyList())
    val clipboard: Clipboard = LocalClipboard.current
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
                tags = viewModel.tags,
                onEditTags = {
                    viewModel.isTagDialogVisible = true
                },
                onSave = {
                    viewModel.save()
                    onNavigateUp()
                },
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
            )
            Headline(
                title = stringResource(R.string.entry_detailsTitle),
                endIcon = painterResource(R.drawable.ic_add),
                onClick = {
                    onCreateDetail(viewModel.entryId)
                }
            )
            if (details.isEmpty()) {
                EmptyPlaceholder(
                    title = stringResource(R.string.entry_emptyPlaceholder_title),
                    subtitle = stringResource(R.string.entry_emptyPlaceholder_subtitle),
                    painter = painterResource(R.drawable.el_entries) //TODO: Change to custom placeholder
                )
            }
            else {
                LazyColumn {
                    items(details) { detail ->
                        DetailListRow(
                            detail = detail,
                            onEditDetail = {
                                onEditDetail(it.id)
                            },
                            onDeleteDetail = {
                                viewModel.detailToDelete = it
                            },
                            onCopyToClipboard = {
                                viewModel.copyToClipboard(it, clipboard)
                            }
                        )
                    }
                }
            }
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
        if (viewModel.detailToDelete != null) {
            ConfirmDeleteDialog(
                text = stringResource(R.string.entry_deleteDetailText, viewModel.detailToDelete!!.name),
                onDismiss = {
                    viewModel.detailToDelete = null
                },
                onConfirm = {
                    val detail: Detail? = viewModel.detailToDelete
                    if (detail != null) {
                        viewModel.deleteDetail(detail)
                        viewModel.detailToDelete = null
                    }
                }
            )
        }
    }
    if (viewModel.isTagDialogVisible) {
        val tagViewModel: TagViewModel = viewModel()
        tagViewModel.init(
            tagRepository = viewModel.tagRepository,
            selectedTags = viewModel.tags
        )
        TagSheet(
            viewModel = tagViewModel,
            onDismiss = {
                viewModel.isTagDialogVisible = false
            },
            onSave = { selectedTags ->
                viewModel.isTagDialogVisible = false
                viewModel.tags.clear()
                viewModel.tags.addAll(selectedTags)
            }
        )
    }
}


@Composable
private fun GeneralSection(
    description: String,
    onEditDescription: () -> Unit,
    name: String,
    tags: List<Tag>,
    onEditTags: () -> Unit,
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
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.padding_horizontal),
                    end = dimensionResource(R.dimen.padding_horizontal),
                    bottom = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
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
        if (tags.isEmpty()) {
            //No tags:
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_horizontal)
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tag),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.entry_tagsEmpty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_horizontal)
                        )
                        .weight(1f)
                )
                IconButton(
                    onClick = onEditTags
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        else {
            //List of tags:
            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tags) { tag ->
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(tag.name)
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun DetailListRow(
    detail: Detail,
    onEditDetail: (Detail) -> Unit,
    onDeleteDetail: (Detail) -> Unit,
    onCopyToClipboard: (Detail) -> Unit
) {
    var isObfuscated: Boolean by remember { mutableStateOf(detail.isObfuscated) }
    var isDropdownVisible: Boolean by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEditDetail(detail)
            }
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                top = dimensionResource(R.dimen.padding_vertical),
                end = dimensionResource(R.dimen.margin_horizontal) - 12.dp,
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(dimensionResource(R.dimen.image_m))
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Icon(
                painter = painterResource(if (detail.icon != null) { detail.icon!!.drawableResourceId } else { detail.type.defaultIcon.drawableResourceId }),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(R.dimen.padding_horizontal))
        ) {
            Text(
                text = detail.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = if (detail.isObfuscated && isObfuscated) { stringResource(R.string.placeholder_obfuscated) } else { detail.content },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            if (detail.isObfuscated) {
                IconButton(
                    onClick = {
                        isObfuscated = !isObfuscated
                    }
                ) {
                    Icon(
                        painter = if (isObfuscated) { painterResource(R.drawable.ic_visibility_on) } else { painterResource(R.drawable.ic_visibility_off) },
                        contentDescription = ""
                    )
                }
            }
            IconButton(
                onClick = {
                    isDropdownVisible = !isDropdownVisible
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_more),
                    contentDescription = ""
                )
                DropdownMenu(
                    expanded = isDropdownVisible,
                    onDismissRequest = {
                        isDropdownVisible = false
                    }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.entry_editDetail))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = ""
                            )
                        },
                        onClick = {
                            isDropdownVisible = false
                            onEditDetail(detail)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.entry_deleteDetail))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = ""
                            )
                        },
                        onClick = {
                            isDropdownVisible = false
                            onDeleteDetail(detail)
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.entry_copyDetail))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_copy),
                                contentDescription = ""
                            )
                        },
                        onClick = {
                            isDropdownVisible = false
                            onCopyToClipboard(detail)
                        }
                    )
                }
            }
        }
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
