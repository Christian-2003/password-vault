package de.christian2003.passwordvault.plugin.presentation.view.tag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.passwordvault.plugin.presentation.ui.composables.TextInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Displays a bottom sheet through which to select tags for an entry.
 *
 * @param viewModel View model from which to source data.
 * @param onDismiss Callback invoked to dismiss the sheet without saving.
 * @param onSave    Callback invoked to dismiss the sheet and save a list of selected tags.
 */
@Composable
fun TagSheet(
    viewModel: TagViewModel,
    onDismiss: () -> Unit,
    onSave: (List<Tag>) -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val tags: List<Tag> by viewModel.tags.collectAsState(emptyList())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.tag_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onDismiss()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cancel),
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onSave(viewModel.selectedTags)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.button_save))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )

            HorizontalDivider()
            if (tags.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    EmptyPlaceholder(
                        title = stringResource(R.string.tag_emptyPlaceholder_title),
                        subtitle = stringResource(R.string.tag_emptyPlaceholder_subtitle),
                        painter = painterResource(R.drawable.el_tags),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            else {
                TagList(
                    tags = tags,
                    selectedTags = viewModel.selectedTags,
                    onTagSelected = { tag ->
                        viewModel.selectedTags.add(tag)
                    },
                    onTagDeselected = { tag ->
                        viewModel.selectedTags.remove(tag)
                    },
                    onEditTag = { tag ->
                        viewModel.tagToEdit = tag
                    },
                    onDeleteTag = { tag ->
                        viewModel.tagToDelete = tag
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()
            TextButton(
                onClick = {
                    viewModel.isCreateTagDialogVisible = true
                },
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(R.dimen.margin_horizontal),
                        vertical = dimensionResource(R.dimen.padding_vertical)
                    )
                    .align(Alignment.End)
            ) {
                Text(stringResource(R.string.button_createTag))
            }
        }
    }

    if (viewModel.tagToDelete != null) {
        ConfirmDeleteDialog(
            text = stringResource(R.string.tag_deleteTagText, viewModel.tagToDelete?.name ?: ""),
            onDismiss = {
                viewModel.tagToDelete = null
            },
            onConfirm = {
                viewModel.deleteTag()
            }
        )
    }
    if (viewModel.tagToEdit != null) {
        EditTagDialog(
            tag = viewModel.tagToEdit,
            onDismiss = {
                viewModel.tagToEdit = null
            },
            onSave = { tag ->
                viewModel.tagToEdit = null
                viewModel.saveTag(tag)
            }
        )
    }
    if (viewModel.isCreateTagDialogVisible) {
        EditTagDialog(
            tag = null,
            onDismiss = {
                viewModel.isCreateTagDialogVisible = false
            },
            onSave = { tag ->
                viewModel.isCreateTagDialogVisible = false
                viewModel.createTag(tag)
            }
        )
    }
}


/**
 * Displays a list of all tags.
 *
 * @param tags              List of all tags.
 * @param selectedTags      List of all tags that are currently selected.
 * @param onTagSelected     Callback invoked once a tag is selected.
 * @param onTagDeselected   Callback invoked once a tag is deselected.
 * @param onEditTag         Callback invoked to edit a tag.
 * @param onDeleteTag       Callback invoked to delete a tag.
 * @param modifier          Modifier.
 */
@Composable
private fun TagList(
    tags: List<Tag>,
    selectedTags: List<Tag>,
    onTagSelected: (Tag) -> Unit,
    onTagDeselected: (Tag) -> Unit,
    onEditTag: (Tag) -> Unit,
    onDeleteTag: (Tag) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        items(tags) { tag ->
            TagListRow(
                tag = tag,
                selected = selectedTags.contains(tag),
                onSelectedChange = { isSelected ->
                    if (isSelected) {
                        onTagSelected(tag)
                    }
                    else {
                        onTagDeselected(tag)
                    }
                },
                onEditTag = onEditTag,
                onDeleteTag = onDeleteTag
            )
        }
    }
}


/**
 * Displays a single tag within the tags list.
 *
 * @param tag               Tag to display.
 * @param selected          Whether the tag is currently selected.
 * @param onSelectedChange  Callback invoked once the tag selection has changed.
 * @param onEditTag         Callback invoked to edit a tag.
 * @param onDeleteTag       Callback invoked to delete a tag.
 */
@Composable
private fun TagListRow(
    tag: Tag,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    onEditTag: (Tag) -> Unit,
    onDeleteTag: (Tag) -> Unit
) {
    var isDropdownVisible: Boolean by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelectedChange(!selected)
            }
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal) - 12.dp,
                top = dimensionResource(R.dimen.padding_vertical),
                end = dimensionResource(R.dimen.margin_horizontal) - 12.dp,
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = onSelectedChange
        )
        Text(
            text = tag.name,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
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
                        Text(stringResource(R.string.tag_editTag))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = ""
                        )
                    },
                    onClick = {
                        isDropdownVisible = false
                        onEditTag(tag)
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(stringResource(R.string.tag_deleteTag))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = ""
                        )
                    },
                    onClick = {
                        isDropdownVisible = false
                        onDeleteTag(tag)
                    }
                )
            }
        }
    }
}


/**
 * Displays a dialog through which to edit or create a tag.
 *
 * @param tag       Tag to edit. Pass null in order to create a new tag.
 * @param onDismiss Callback invoked to dismiss the dialog without saving the tag.
 * @param onSave    Callback invoked to dismiss the dialog and save the tag which has been edited
 *                  or created.
 */
@Composable
private fun EditTagDialog(
    tag: Tag?,
    onDismiss: () -> Unit,
    onSave: (Tag) -> Unit
) {
    var name: String by remember { mutableStateOf(tag?.name ?: "") }
    val nameError: String = stringResource(R.string.tag_nameError)
    var isNameErrorVisible: Boolean by remember { mutableStateOf(false) }
    val focusRequester: FocusRequester = remember { FocusRequester() }

    val onSaveClick: () -> Unit = {
        if (name.isNotBlank()) {
            if (tag != null) {
                tag.name = name
                onSave(tag)
            }
            else {
                val newTag = Tag(
                    name = name
                )
                onSave(newTag)
            }
        }
        else {
            isNameErrorVisible = true
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = if (tag == null) { stringResource(R.string.tag_titleCreate) } else { stringResource(R.string.tag_titleEdit) },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                TextInput(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = stringResource(R.string.tag_nameLabel),
                    errorMessage = if (isNameErrorVisible) { nameError } else { null },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onSaveClick()
                        }
                    ),
                    focusRequester = focusRequester,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                    TextButton(
                        onClick = {
                            onSaveClick()
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(stringResource(R.string.button_save))
                    }
                }
            }
        }
    }
}
