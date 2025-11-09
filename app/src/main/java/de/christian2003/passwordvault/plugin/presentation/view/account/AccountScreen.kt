package de.christian2003.passwordvault.plugin.presentation.view.account

import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ConfirmDiscardDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ContextAction
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ContextActionBase
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ContextActionDivider
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ContextActions
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EditValueDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Eyecatcher
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Headline
import de.christian2003.passwordvault.plugin.presentation.ui.composables.HelpCard
import de.christian2003.passwordvault.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Tooltip
import de.christian2003.passwordvault.plugin.presentation.view.account.detail.DetailSheet
import de.christian2003.passwordvault.plugin.presentation.view.account.detail.DetailViewModel
import de.christian2003.passwordvault.plugin.presentation.view.account.tag.TagSheet
import de.christian2003.passwordvault.plugin.presentation.view.account.tag.TagViewModel
import de.christian2003.passwordvault.plugin.presentation.view.account.target.TargetSheet
import de.christian2003.passwordvault.plugin.presentation.view.account.target.TargetViewModel
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.text.ifEmpty


/**
 * Screen displays an account to the user.
 *
 * @param viewModel     View model for the screen.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    onNavigateUp: () -> Unit
) {
    val appBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val lazyListState: LazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val offset = 1 //Number of items in LazyColumn that are above the visible details list
        viewModel.reorderDetails(from.index - offset, to.index - offset)
    }
    val selectedTags: List<TagUiDto> by viewModel.selectedTags.collectAsState(emptyList())

    val invokeOnNavigateUp: () -> Unit = {
        if(viewModel.areChangesMade(selectedTags)) {
            viewModel.visibleDialog = AccountScreenDialog.Discard
        }
        else {
            onNavigateUp()
        }
    }

    BackHandler {
        when (viewModel.screenState) {
            ScreenState.Multiselect -> viewModel.dismissMultiselectState()
            ScreenState.Reorder -> viewModel.dismissReorderableState()
            else -> invokeOnNavigateUp()
        }
    }

    Scaffold(
        topBar = {
            when (viewModel.screenState) {
                ScreenState.Reorder -> ReorderAppBar(
                    helpState = viewModel.helpState,
                    onFinishReordering = {
                        viewModel.dismissReorderableState()
                    }
                )
                ScreenState.Multiselect -> MultiselectAppBar(
                    selectedDetailsCount = viewModel.selectedDetailIds.size,
                    helpState = viewModel.helpState,
                    onSelectAll = {
                        viewModel.selectAllDetails()
                    },
                    onDeleteSelected = {
                        viewModel.visibleDialog = AccountScreenDialog.DeleteDetailMultiselect
                    },
                    onFinishMultiselect = {
                        viewModel.dismissMultiselectState()
                    }
                )
                else -> DefaultAppBar(
                    name = viewModel.name,
                    helpState = viewModel.helpState,
                    scrollBehavior = scrollBehavior,
                    onNavigateUp = invokeOnNavigateUp,
                    onEditName = {
                        viewModel.visibleDialog = AccountScreenDialog.Name
                    },
                    onEditDescription = {
                        viewModel.visibleDialog = AccountScreenDialog.Description
                    },
                    onSelectTargets = {
                        viewModel.visibleDialog = AccountScreenDialog.Target
                    },
                    onSelectTags = {
                        viewModel.visibleDialog = AccountScreenDialog.Tag
                    },
                    onCreateDetail = {
                        viewModel.visibleDialog = AccountScreenDialog.Detail
                    },
                    onReorderDetails = {
                        viewModel.startReorderableState()
                    }
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .fillMaxSize()
        ) {
            item {
                GeneralSection(
                    description = viewModel.description,
                    onEditDescription = {
                        viewModel.visibleDialog = AccountScreenDialog.Description
                    },
                    name = viewModel.name,
                    tags = selectedTags,
                    icon = viewModel.icon.value,
                    screenState = viewModel.screenState,
                    helpState = viewModel.helpState,
                    isDataValid = viewModel.isDataValid.value,
                    onEditTags = {
                        viewModel.visibleDialog = AccountScreenDialog.Tag
                    },
                    onEditTargets = {
                        viewModel.visibleDialog = AccountScreenDialog.Target
                    },
                    onSave = {
                        viewModel.save()
                        onNavigateUp()
                    },
                    onDismissHelpCard = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                )
                HorizontalDivider()
                Headline(
                    title = stringResource(R.string.account_details_title),
                    endIcon = painterResource(R.drawable.ic_add),
                    isEyecatcherVisible = viewModel.helpState == AccountScreenHelpState.Details,
                    onClick = {
                        viewModel.visibleDialog = AccountScreenDialog.Detail
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                )
            }
            if (viewModel.details.isEmpty()) {
                item {
                    EmptyPlaceholder(
                        title = stringResource(R.string.account_details_emptyPlaceholder_title),
                        subtitle = stringResource(R.string.account_details_emptyPlaceholder_subtitle),
                        painter = painterResource(R.drawable.el_details)
                    )
                }
            }
            else {
                items(
                    items = viewModel.visibleDetails.value,
                    key = { it.id }
                ) { detail ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = detail.id
                    ) { isDragging ->
                        val detailListRowModifier: Modifier = if (isDragging) {
                            Modifier
                                .draggableHandle()
                                .shadow(16.dp)
                        } else {
                            Modifier
                        }
                        DetailListRow(
                            detail = detail,
                            screenState = viewModel.screenState,
                            onEdit = {
                                viewModel.detailToEdit = it
                            },
                            onDelete = {
                                viewModel.detailToDelete = it
                            },
                            onCopyToClipboard = {
                                viewModel.copyToClipboard(it)
                            },
                            onReorderDetails = {
                                viewModel.startReorderableState()
                            },
                            onMultiselect = { detail ->
                                viewModel.startMultiselectState(detail.id)
                            },
                            onToggleSelection = { detail, selected ->
                                if (selected) {
                                    viewModel.selectedDetailIds.add(detail.id)
                                }
                                else {
                                    viewModel.selectedDetailIds.remove(detail.id)
                                    if (viewModel.selectedDetailIds.isEmpty()) {
                                        viewModel.dismissMultiselectState()
                                    }
                                }
                            },
                            isDetailSelected = { detail ->
                                viewModel.isDetailSelected(detail.id)
                            },
                            modifier = detailListRowModifier
                        )
                    }
                }
                if (viewModel.invisibleDetails.value.isNotEmpty()) {
                    item {
                        val animatedArrowRotation by animateFloatAsState(
                            targetValue = if (viewModel.areInvisibleDetailsVisible) { 180F } else { 0F },
                            animationSpec = spring()
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.areInvisibleDetailsVisible = !viewModel.areInvisibleDetailsVisible
                                },
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_expand),
                                        contentDescription = "",
                                        modifier = Modifier.rotate(animatedArrowRotation)
                                    )
                                    Text(
                                        text = if (viewModel.areInvisibleDetailsVisible) {
                                            stringResource(R.string.button_showLess)
                                        } else {
                                            stringResource(R.string.button_showMore)
                                        },
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                if (viewModel.areInvisibleDetailsVisible) {
                    items(
                        items = viewModel.invisibleDetails.value,
                        key = { it.id }
                    ) { detail ->
                        ReorderableItem(
                            state = reorderableLazyListState,
                            key = detail.id
                        ) { isDragging ->
                            val detailListRowModifier: Modifier = if (isDragging) {
                                Modifier
                                    .draggableHandle()
                                    .shadow(16.dp)
                            } else {
                                Modifier
                            }
                            DetailListRow(
                                detail = detail,
                                screenState = viewModel.screenState,
                                onEdit = {
                                    viewModel.detailToEdit = it
                                },
                                onDelete = {
                                    viewModel.detailToDelete = it
                                },
                                onCopyToClipboard = {
                                    viewModel.copyToClipboard(it)
                                },
                                onReorderDetails = {
                                    viewModel.startReorderableState()
                                },
                                onMultiselect = { detail ->
                                    viewModel.startMultiselectState(detail.id)
                                },
                                onToggleSelection = { detail, selected ->
                                    if (selected) {
                                        viewModel.selectedDetailIds.add(detail.id)
                                    }
                                    else {
                                        viewModel.selectedDetailIds.remove(detail.id)
                                        if (viewModel.selectedDetailIds.isEmpty()) {
                                            viewModel.dismissMultiselectState()
                                        }
                                    }
                                },
                                isDetailSelected = { detail ->
                                    viewModel.isDetailSelected(detail.id)
                                },
                                modifier = detailListRowModifier
                            )
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier.height(innerPadding.calculateBottomPadding())
                    )
                }
            }
        }

        NavigationBarProtection(
            color = MaterialTheme.colorScheme.surfaceContainer.copy(0.5f),
            windowInsets = WindowInsets(bottom = innerPadding.calculateBottomPadding())
        )

    }


    //Dialogs:
    when (viewModel.visibleDialog) {
        AccountScreenDialog.Name -> {
            val errorBlankInput: String = stringResource(R.string.error_blankInput)
            EditValueDialog(
                value = viewModel.name,
                onValidateValue = { value ->
                    if (value.isBlank()) {
                        errorBlankInput
                    }
                    else {
                        null
                    }
                },
                label = stringResource(R.string.account_nameLabel),
                title = stringResource(R.string.account_nameTitle),
                onDismiss = {
                    viewModel.dismissNameDialog()
                },
                onSave = { name ->
                    viewModel.dismissNameDialog(name)
                }
            )
        }
        AccountScreenDialog.Description -> {
            EditValueDialog(
                value = viewModel.description,
                onValidateValue = { value -> null},
                label = stringResource(R.string.account_descriptionLabel),
                title = stringResource(R.string.account_descriptionTitle),
                onDismiss = {
                    viewModel.dismissDescriptionDialog()
                },
                onSave = { description ->
                    viewModel.dismissDescriptionDialog(description)
                }
            )
        }
        AccountScreenDialog.DeleteDetailMultiselect -> {
            ConfirmDeleteDialog(
                title = pluralStringResource(R.plurals.account_details_confirmDeleteTitleMultiselect, viewModel.selectedDetailIds.size),
                text = pluralStringResource(R.plurals.account_details_confirmDeleteTextMultiselect, viewModel.selectedDetailIds.size, viewModel.selectedDetailIds.size),
                confirmButtonText = stringResource(R.string.button_remove),
                onDismiss = {
                    viewModel.dismissDeleteDetailsMultiselectDialog()
                },
                onConfirm = {
                    viewModel.dismissDeleteDetailsMultiselectDialog(viewModel.selectedDetailIds)
                }
            )
        }
        AccountScreenDialog.Tag -> {
            val tagViewModel: TagViewModel = hiltViewModel(key = "vm_${viewModel.viewModelStoreId}")
            viewModel.initTagViewModel(tagViewModel, selectedTags)
            TagSheet(
                viewModel = tagViewModel,
                onDismiss = {
                    viewModel.dismissTagDialog()
                },
                onSave = { selectedTagIds ->
                    viewModel.dismissTagDialog(selectedTagIds)
                }
            )
        }
        AccountScreenDialog.Target -> {
            val targetViewModel: TargetViewModel = hiltViewModel(key = "vm_${viewModel.viewModelStoreId}")
            viewModel.initTargetViewModel(targetViewModel)
            TargetSheet(
                viewModel = targetViewModel,
                onDismiss = {
                    viewModel.dismissTargetDialog()
                },
                onSave = { targets ->
                    viewModel.dismissTargetDialog(targets)
                }
            )
        }
        AccountScreenDialog.Detail -> {
            val detailViewModel: DetailViewModel = hiltViewModel(key = "vm_${viewModel.viewModelStoreId}")
            detailViewModel.init(null)
            DetailSheet(
                viewModel = detailViewModel,
                onDismiss = {
                    viewModel.dismissDetailDialog()
                },
                onSave = { detail ->
                    viewModel.dismissDetailDialog(detail)
                }
            )
        }
        AccountScreenDialog.Discard -> {
            ConfirmDiscardDialog(
                text = stringResource(R.string.account_discardChanges),
                onDismiss = {
                    viewModel.visibleDialog = AccountScreenDialog.None
                },
                onConfirm = {
                    viewModel.visibleDialog = AccountScreenDialog.None
                    onNavigateUp()
                }
            )
        }
        else -> {
            //Dialog to delete a detail:
            if (viewModel.detailToDelete != null) {
                ConfirmDeleteDialog(
                    title = stringResource(R.string.account_details_confirmDeleteTitle),
                    text = stringResource(R.string.account_details_confirmDeleteText, viewModel.detailToDelete!!.name),
                    confirmButtonText = stringResource(R.string.button_remove),
                    onDismiss = {
                        viewModel.dismissDeleteDetailDialog()
                    },
                    onConfirm = {
                        viewModel.dismissDeleteDetailDialog(viewModel.detailToDelete)
                    }
                )
            }

            //Dialog to edit a detail:
            if (viewModel.detailToEdit != null) {
                val detailViewModel: DetailViewModel = hiltViewModel(key = "vm_${viewModel.viewModelStoreId}")
                detailViewModel.init(viewModel.detailToEdit)
                DetailSheet(
                    viewModel = detailViewModel,
                    onDismiss = {
                        viewModel.dismissDetailDialog()
                    },
                    onSave = { detail ->
                        viewModel.dismissDetailDialog(detail)
                    }
                )
            }
        }
    }
}


/**
 * Default app bar displayed in default state.
 *
 * @param name              Name of the account.
 * @param helpState         Help state.
 * @param scrollBehavior    Scroll behavior for the screen.
 * @param onNavigateUp      Callback invoked to navigate up the navigation stack.
 * @param onEditName        Callback invoked to edit the account name.
 * @param onEditDescription Callback invoked to edit the account description.
 * @param onSelectTargets   Callback invoked to edit the targets.
 * @param onSelectTags      Callback invoked to edit the tags.
 * @param onCreateDetail    Callback invoked to create a new detail.
 * @param onReorderDetails  Callback invoked to reorder details.
 */
@Composable
private fun DefaultAppBar(
    name: String,
    helpState: AccountScreenHelpState?,
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigateUp: () -> Unit,
    onEditName: () -> Unit,
    onEditDescription: () -> Unit,
    onSelectTargets: () -> Unit,
    onSelectTags: () -> Unit,
    onCreateDetail: () -> Unit,
    onReorderDetails: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    //Text has horizontal padding of 8 dp, so that the touch target size is larger.
                    //However, this increases the space between the back-icon and the text by 8 dp.
                    //To prevent this unusual space that irritates the user, we offset the text by -8 dp.
                    .offset(x = (-8).dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable {
                        onEditName()
                    }
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
            ) {
                Text(
                    text = name.ifEmpty { stringResource(R.string.account_namePlaceholder) },
                    color = if (!name.isEmpty()) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                if (helpState == AccountScreenHelpState.Name) {
                    Eyecatcher()
                }
            }
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
        },
        actions = {
            ContextActions(
                actions = listOf(
                    ContextAction(
                        text = stringResource(R.string.account_contextActions_editName),
                        icon = painterResource(R.drawable.ic_edit_name)
                    ) {
                        onEditName()
                    },
                    ContextAction(
                        text = stringResource(R.string.account_contextActions_editDescription),
                        icon = painterResource(R.drawable.ic_edit_description)
                    ) {
                        onEditDescription()
                    },
                    ContextAction(
                        text = stringResource(R.string.account_contextActions_selectAutofillTargets),
                        icon = painterResource(R.drawable.ic_website)
                    ) {
                        onSelectTargets()
                    },
                    ContextAction(
                        text = stringResource(R.string.account_contextActions_selectTags),
                        icon = painterResource(R.drawable.ic_tag)
                    ) {
                        onSelectTags()
                    },
                    ContextActionDivider(),
                    ContextAction(
                        text = stringResource(R.string.account_contextActions_createDetail),
                        icon = painterResource(R.drawable.ic_add)
                    ) {
                        onCreateDetail()
                    },
                    ContextAction(
                        text = stringResource(R.string.account_contextActions_reorderDetails),
                        icon = painterResource(R.drawable.ic_reorder)
                    ) {
                        onReorderDetails()
                    }
                )
            )
        },
        scrollBehavior = scrollBehavior
    )
}


/**
 * App bar for the reorder state.
 *
 * @param helpState             Help state.
 * @param onFinishReordering    Callback invoked to finish reordering state.
 */
@Composable
private fun ReorderAppBar(
    helpState: AccountScreenHelpState?,
    onFinishReordering: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.account_titleReorder),
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onFinishReordering
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cancel),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                if (helpState != null) {
                    Eyecatcher(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            end = 8.dp
                        )
                    )
                }
            }

        },
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    )
}


/**
 * App bar for the multiselect state.
 *
 * @param selectedDetailsCount  Number of details currently selected.
 * @param helpState             Help state.
 * @param onSelectAll           Callback invoked to select all details.
 * @param onDeleteSelected      Callback invoked to delete all selected details.
 * @param onFinishMultiselect   Callback invoked to finish multiselect state.
 */
@Composable
private fun MultiselectAppBar(
    selectedDetailsCount: Int,
    helpState: AccountScreenHelpState?,
    onSelectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onFinishMultiselect: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = pluralStringResource(R.plurals.account_titleMultiselect, selectedDetailsCount, selectedDetailsCount),
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                Tooltip(
                    tooltip = stringResource(R.string.tooltip_closeMultiselect),
                    anchor = TooltipAnchorPosition.End
                ) {
                    IconButton(
                        onClick = onFinishMultiselect
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cancel),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                if (helpState != null) {
                    Eyecatcher(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            end = 8.dp
                        )
                    )
                }
            }
        },
        actions = {
            Tooltip(
                tooltip = stringResource(R.string.tooltip_selectAllDetails),
                anchor = TooltipAnchorPosition.Start
            ) {
                IconButton(
                    onClick = onSelectAll
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_selectall),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Tooltip(
                tooltip = stringResource(R.string.tooltip_deleteSelectedDetails),
                anchor = TooltipAnchorPosition.Start
            ) {
                IconButton(
                    onClick = onDeleteSelected
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    )
}


/**
 * Displays the general section at the top of the page.
 *
 * @param description           Description of the account.
 * @param name                  Name of the account.
 * @param tags                  List of tags of the account.
 * @param icon                  Icon for the account.
 * @param screenState           Screen state.
 * @param helpState             Help state indicating the help message to display.
 * @param isDataValid           Whether the data entered by the user is valid.
 * @param onEditDescription     Callback invoked to edit the description.
 * @param onEditTags            Callback invoked to edit the tags.
 * @param onEditTargets         Callback invoked to edit the targets.
 * @param onSave                Callback invoked to save the changes.
 * @param onDismissHelpCard     Callback invoked to dismiss the help card.
 * @param modifier              Modifier.
 */
@Composable
private fun GeneralSection(
    description: String,
    name: String,
    tags: List<TagUiDto>,
    icon: Drawable?,
    screenState: ScreenState,
    helpState: AccountScreenHelpState?,
    isDataValid: Boolean,
    onEditDescription: () -> Unit,
    onEditTags: () -> Unit,
    onEditTargets: () -> Unit,
    onSave: () -> Unit,
    onDismissHelpCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (helpState != null) {
        HelpCard(
            text = when (screenState) {
                ScreenState.Multiselect -> stringArrayResource(R.array.account_helpMessages)[AccountScreenHelpState.CloseMultiselect.ordinal]
                ScreenState.Reorder -> stringArrayResource(R.array.account_helpMessages)[AccountScreenHelpState.CloseReorder.ordinal]
                else -> stringArrayResource(R.array.account_helpMessages)[helpState.ordinal]
            },
            onDismiss = onDismissHelpCard,
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
        )
    }
    Row(
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
        ) {
            if (icon == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_xl))
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .clickable {
                            onEditTargets()
                        }
                ) {
                    Text(
                        text = if (!name.isEmpty()) { name.first().toString() } else { "?" },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }
            }
            else {
                Image(
                    painter = rememberDrawablePainter(icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.image_xl))
                        .clip(MaterialTheme.shapes.medium)
                        .clickable {
                            onEditTargets()
                        }
                )
            }
            if (helpState == AccountScreenHelpState.Targets) {
                Eyecatcher()
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
            ) {
                Text(
                    text = description.ifEmpty { stringResource(R.string.account_descriptionPlaceholder) },
                    color = if (!description.isEmpty()) { MaterialTheme.colorScheme.onSurface } else { MaterialTheme.colorScheme.onSurface.copy(0.5f) },
                    style = MaterialTheme.typography.bodyLarge
                )
                if (helpState == AccountScreenHelpState.Description) {
                    Eyecatcher()
                }
            }
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
            ) {
                Button(
                    onClick = onSave,
                    enabled = isDataValid
                ) {
                    Text(stringResource(R.string.button_save))
                }
                if (helpState == AccountScreenHelpState.Save) {
                    Eyecatcher()
                }
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
                    start = dimensionResource(R.dimen.margin_horizontal),
                    end = dimensionResource(R.dimen.margin_horizontal) - 12.dp
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_tag),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.account_tags_emptyPlaceholder_title),
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
                    onClick = {
                        onEditTags()
                    },
                    label = {
                        Text(tag.name)
                    },
                    modifier = Modifier
                        .padding(
                            start = if (tags.indexOf(tag) == 0) {
                                dimensionResource(R.dimen.margin_horizontal)
                            } else {
                                0.dp
                            },
                            end = if (tags.indexOf(tag) == tags.size - 1 ) {
                                dimensionResource(R.dimen.margin_horizontal)
                            } else {
                                dimensionResource(R.dimen.padding_horizontal)
                            }
                        )
                )
            }
        }
    }
}


/**
 * Displays a single account detail in a list row.
 *
 * @param detail                Detail to display.
 * @param screenState           Screen state.
 * @param onEdit                Callback invoked to edit the detail.
 * @param onDelete              Callback invoked to delete the detail.
 * @param onCopyToClipboard     Callback invoked to copy the detail content to the clipboard.
 * @param onReorderDetails      Callback invoked to begin reordering the details.
 * @param onMultiselect         Callback invoked to begin selecting details.
 * @param isDetailSelected      Callback invoked to determine whether a detail is selected.
 * @param modifier              Modifier.
 */
@Composable
private fun ReorderableCollectionItemScope.DetailListRow(
    detail: Detail,
    screenState: ScreenState,
    onEdit: (Detail) -> Unit,
    onDelete: (Detail) -> Unit,
    onCopyToClipboard: (Detail) -> Unit,
    onReorderDetails: () -> Unit,
    onMultiselect: (Detail) -> Unit,
    onToggleSelection: (Detail, Boolean) -> Unit,
    isDetailSelected: (Detail) -> Boolean,
    modifier: Modifier = Modifier
) {
    var isObfuscated: Boolean by remember { mutableStateOf(detail.metadata.isObfuscated) }
    val isSelected: Boolean = if (screenState == ScreenState.Multiselect) {
        isDetailSelected(detail)
    } else {
        false
    }
    val quarterVerticalPadding: Dp = dimensionResource(R.dimen.padding_vertical) / 4

    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (screenState == ScreenState.Multiselect) {
                        onToggleSelection(detail, !isSelected)
                    } else {
                        onEdit(detail)
                    }
                },
                onLongClick = if (screenState == ScreenState.Default) {
                    { onMultiselect(detail) }
                } else {
                    null
                }
            )
            .padding(
                vertical = quarterVerticalPadding
            )
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                top = quarterVerticalPadding * 3,
                end = dimensionResource(R.dimen.margin_horizontal) - 12.dp,
                bottom = quarterVerticalPadding * 3
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
                modifier = Modifier.size(dimensionResource(R.dimen.image_s))
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
                text = if (detail.metadata.isObfuscated && isObfuscated) { stringResource(R.string.placeholder_obfuscated) } else { detail.content },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        when (screenState) {
            ScreenState.Reorder -> IconButton(
                onClick = { },
                modifier = Modifier
                    .draggableHandle()
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_draghandle),
                    contentDescription = "",
                )
            }
            ScreenState.Multiselect -> RadioButton(
                selected = isSelected,
                onClick = {
                    onToggleSelection(detail, !isSelected)
                }
            )
            else -> Row(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                if (detail.metadata.isObfuscated) {
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

                val contextActions: MutableList<ContextActionBase> = mutableListOf(
                    ContextAction(
                        text = stringResource(R.string.account_details_edit),
                        icon = painterResource(R.drawable.ic_edit)
                    ) {
                        onEdit(detail)
                    },
                    ContextAction(
                        text = stringResource(R.string.account_details_delete),
                        icon = painterResource(R.drawable.ic_delete)
                    ) {
                        onDelete(detail)
                    },
                    ContextActionDivider(),
                    ContextAction(
                        text = stringResource(R.string.account_details_copyToClipboard),
                        icon = painterResource(R.drawable.ic_copy)
                    ) {
                        onCopyToClipboard(detail)
                    },
                    ContextAction(
                        text = stringResource(R.string.account_details_reorder),
                        icon = painterResource(R.drawable.ic_reorder)
                    ) {
                        onReorderDetails()
                    }
                )
                if (detail.metadata.isObfuscated) {
                    contextActions.add(ContextAction(
                        text = if (isObfuscated) { stringResource(R.string.account_details_showContent) } else { stringResource(R.string.account_details_hideContent) },
                        icon = if (isObfuscated) { painterResource(R.drawable.ic_visibility_on) } else { painterResource(R.drawable.ic_visibility_off) }
                    ) {
                        isObfuscated = !isObfuscated
                    })
                }
                ContextActions(contextActions)
            }
        }
    }
}
