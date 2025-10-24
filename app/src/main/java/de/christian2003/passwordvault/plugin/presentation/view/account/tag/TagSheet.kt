package de.christian2003.passwordvault.plugin.presentation.view.account.tag

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ConfirmDiscardDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EditValueDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.passwordvault.plugin.presentation.ui.composables.HelpCard
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Tooltip
import de.christian2003.passwordvault.plugin.presentation.view.account.TagUiDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid


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
    onSave: (Set<Uuid>) -> Unit
) {
    val tags: List<TagUiDto> by viewModel.tags.collectAsState(emptyList())
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val invokeOnDismiss: () -> Unit = {
        if (viewModel.areChangesMade(tags)) {
            viewModel.isDiscardDialogVisible = true
        }
        else {
            coroutineScope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                onDismiss()
            }
        }
    }


    ModalBottomSheet(
        onDismissRequest = invokeOnDismiss,
        sheetState = sheetState,
        dragHandle = null,
        sheetGesturesEnabled = false,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        )
    ) {
        BackHandler {
            invokeOnDismiss()
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.tag_title))
                },
                navigationIcon = {
                    Tooltip(
                        tooltip = stringResource(R.string.tooltip_closeWithoutSaving),
                        anchor = TooltipAnchorPosition.End
                    ) {
                        IconButton(
                            onClick = invokeOnDismiss
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_cancel),
                                contentDescription = ""
                            )
                        }
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onSave(viewModel.selectedTagIds)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.button_ok))
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
                    AnimatedVisibility(viewModel.isHelpCardVisible) {
                        HelpCard(
                            text = stringResource(R.string.tag_helpCreate),
                            onDismiss = {
                                viewModel.dismissHelpCard()
                            },
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(R.dimen.margin_horizontal),
                                vertical = dimensionResource(R.dimen.padding_vertical)
                            )
                        )
                    }
                    val modifier: Modifier = if (viewModel.isHelpCardVisible) { Modifier } else { Modifier.weight(1f) }
                    EmptyPlaceholder(
                        title = stringResource(R.string.tag_emptyPlaceholder_title),
                        subtitle = stringResource(R.string.tag_emptyPlaceholder_subtitle),
                        painter = painterResource(R.drawable.el_tags),
                        modifier = modifier
                    )
                }
            }
            else {
                TagList(
                    tags = tags,
                    isHelpCardVisible = viewModel.isHelpCardVisible,
                    onTagSelected = { tag ->
                        viewModel.selectTag(tag.id)
                    },
                    onTagDeselected = { tag ->
                        viewModel.deselectTag(tag.id)
                    },
                    onEditTag = { tag ->
                        viewModel.tagToEdit = tag
                    },
                    onDeleteTag = { tag ->
                        viewModel.tagToDelete = tag
                    },
                    isTagSelected = { tag ->
                        viewModel.isTagSelected(tag.id)
                    },
                    dismissHelpCard = {
                        viewModel.dismissHelpCard()
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
            onSave = { tagName ->
                viewModel.saveTag(tagName)
            }
        )
    }

    if (viewModel.isCreateTagDialogVisible) {
        EditTagDialog(
            tag = null,
            onDismiss = {
                viewModel.isCreateTagDialogVisible = false
            },
            onSave = { tagName ->
                viewModel.isCreateTagDialogVisible = false
                viewModel.createTag(tagName)
            }
        )
    }

    if (viewModel.isDiscardDialogVisible) {
        ConfirmDiscardDialog(
            text = stringResource(R.string.tag_discardChanges),
            onDismiss = {
                viewModel.isDiscardDialogVisible = false
            },
            onConfirm = {
                coroutineScope.launch {
                    viewModel.isDiscardDialogVisible = false
                    sheetState.hide()
                }.invokeOnCompletion {
                    onDismiss()
                }
            }
        )
    }
}


/**
 * Displays a list of all tags.
 *
 * @param tags              List of all tags.
 * @param isHelpCardVisible Whether the help card is visible.
 * @param onTagSelected     Callback invoked once a tag is selected.
 * @param onTagDeselected   Callback invoked once a tag is deselected.
 * @param onEditTag         Callback invoked to edit a tag.
 * @param onDeleteTag       Callback invoked to delete a tag.
 * @param isTagSelected     Callback invoked to determine whether the specified tag is selected.
 * @param dismissHelpCard   Callback invoked to dismiss the help card.
 * @param modifier          Modifier.
 */
@Composable
private fun TagList(
    tags: List<TagUiDto>,
    isHelpCardVisible: Boolean,
    onTagSelected: (TagUiDto) -> Unit,
    onTagDeselected: (TagUiDto) -> Unit,
    onEditTag: (TagUiDto) -> Unit,
    onDeleteTag: (TagUiDto) -> Unit,
    isTagSelected: (TagUiDto) -> Boolean,
    dismissHelpCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            AnimatedVisibility(isHelpCardVisible) {
                HelpCard(
                    text = stringResource(R.string.tag_helpSelect),
                    onDismiss = dismissHelpCard,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.margin_horizontal),
                        vertical = dimensionResource(R.dimen.padding_vertical)
                    )
                )
            }
        }
        items(tags) { tag ->
            TagListRow(
                tag = tag,
                selected = isTagSelected(tag),
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
    tag: TagUiDto,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    onEditTag: (TagUiDto) -> Unit,
    onDeleteTag: (TagUiDto) -> Unit
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
    tag: TagUiDto?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val errorBlankInput = stringResource(R.string.error_blankInput)
    EditValueDialog(
        value = tag?.name ?: "",
        onValidateValue = { value ->
            if (value.isBlank()) {
                errorBlankInput
            }
            else {
                null
            }
        },
        label = stringResource(R.string.tag_nameLabel),
        title = if (tag == null) { stringResource(R.string.tag_titleCreate) } else { stringResource(R.string.tag_titleEdit) },
        onDismiss = onDismiss,
        onSave = onSave,
        primaryButtonText = stringResource(R.string.button_save)
    )
}
