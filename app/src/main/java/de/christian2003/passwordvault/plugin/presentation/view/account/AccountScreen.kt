package de.christian2003.passwordvault.plugin.presentation.view.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.application.repository.PackagesRepository
import de.christian2003.passwordvault.application.usecases.packages.GetAllPackagesUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetLocalizedPackageNameUseCase
import de.christian2003.passwordvault.application.usecases.packages.GetPackageIconUseCase
import de.christian2003.passwordvault.application.usecases.tag.CreateTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.DeleteTagUseCase
import de.christian2003.passwordvault.application.usecases.tag.GetAllTagsUseCase
import de.christian2003.passwordvault.application.usecases.tag.UpdateTagUseCase
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.plugin.infrastructure.packages.AndroidPackageFingerprintService
import de.christian2003.passwordvault.plugin.infrastructure.packages.LocalPackagesRepository
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EditValueDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Headline
import de.christian2003.passwordvault.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.passwordvault.plugin.presentation.view.account.detail.DetailSheet
import de.christian2003.passwordvault.plugin.presentation.view.account.detail.DetailViewModel
import de.christian2003.passwordvault.plugin.presentation.view.account.tag.TagSheet
import de.christian2003.passwordvault.plugin.presentation.view.account.tag.TagViewModel
import de.christian2003.passwordvault.plugin.presentation.view.account.target.TargetSheet
import de.christian2003.passwordvault.plugin.presentation.view.account.target.TargetViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.text.ifEmpty


@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    onNavigateUp: () -> Unit
) {
    val appBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val lazyListState: LazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val offset = 2 //Number of items in LazyColumn that are above the details list
        viewModel.details.apply {
            this[to.index - offset] = this[from.index - offset].also {
                this[from.index - offset] = this[to.index - offset]
            }
        }
    }
    val details: List<Detail> = viewModel.details
    val selectedTags: List<TagUiDto> by viewModel.selectedTags.collectAsState(emptyList())

    Scaffold(
        topBar = {
            if (viewModel.isInReorderableState) {
                ReorderAppBar(
                    scrollBehavior = scrollBehavior,
                    onFinishReordering = {
                        viewModel.isInReorderableState = false
                    }
                )
            }
            else {
                DefaultAppBar(
                    name = viewModel.name,
                    scrollBehavior = scrollBehavior,
                    onNavigateUp = onNavigateUp,
                    onEditName = {
                        viewModel.isNameDialogVisible = true
                    },
                    onReorderDetails = {
                        viewModel.isInReorderableState = true
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
                        viewModel.isDescriptionDialogVisible = true
                    },
                    name = viewModel.name,
                    tags = selectedTags,
                    onEditTags = {
                        viewModel.isTagDialogVisible = true
                    },
                    onEditTargets = {
                        viewModel.isTargetDialogVisible = true
                    },
                    onSave = {
                        viewModel.save()
                        onNavigateUp()
                    },
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
                )
            }
            stickyHeader {
                HorizontalDivider()
                Headline(
                    title = stringResource(R.string.account_details_title),
                    endIcon = painterResource(R.drawable.ic_add),
                    onClick = {
                        viewModel.isDetailDialogVisible = true
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
                    items = details,
                    key = { it.id }
                ) { detail ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = detail.id
                    ) { isDragging ->
                        val detailListRowModifier: Modifier = if (viewModel.isInReorderableState) {
                            if (isDragging) {
                                Modifier.draggableHandle().shadow(16.dp)
                            } else {
                                Modifier.draggableHandle()
                            }
                        } else {
                            Modifier
                        }
                        DetailListRow(
                            detail = detail,
                            isInReorderableState = viewModel.isInReorderableState,
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
                                viewModel.isInReorderableState = true
                            },
                            modifier = detailListRowModifier
                        )
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


        //Dialog to edit the account name:
        if (viewModel.isNameDialogVisible) {
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
                    viewModel.isNameDialogVisible = false
                },
                onSave = { name ->
                    viewModel.name = name
                    viewModel.isNameDialogVisible = false
                }
            )
        }

        //Dialog to edit the account description:
        if (viewModel.isDescriptionDialogVisible) {
            EditValueDialog(
                value = viewModel.description,
                onValidateValue = { value -> null},
                label = stringResource(R.string.account_descriptionLabel),
                title = stringResource(R.string.account_descriptionTitle),
                onDismiss = {
                    viewModel.isDescriptionDialogVisible = false
                },
                onSave = { description ->
                    viewModel.description = description
                    viewModel.isDescriptionDialogVisible = false
                }
            )
        }

        //Dialog to delete a detail:
        if (viewModel.detailToDelete != null) {
            ConfirmDeleteDialog(
                text = stringResource(R.string.account_details_confirmDeleteText, viewModel.detailToDelete!!.name),
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

    //Dialog to edit the tags:
    if (viewModel.isTagDialogVisible) {
        val tagViewModel: TagViewModel = viewModel(key = "vm_${viewModel.viewModelStoreId}")
        tagViewModel.init(
            getAllTagsUseCase = GetAllTagsUseCase(viewModel.tagRepository),
            createTagUseCase = CreateTagUseCase(viewModel.tagRepository),
            updateTagUseCase = UpdateTagUseCase(viewModel.tagRepository),
            deleteTagUseCase = DeleteTagUseCase(viewModel.tagRepository),
            selectedTagIds = selectedTags.map { it.id }.toSet()
        )
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

    //Dialog to edit the targets:
    if (viewModel.isTargetDialogVisible) {
        //TODO: Move repo instantiation to MainActivity
        val packagesRepository: PackagesRepository = LocalPackagesRepository(LocalContext.current.packageManager)
        val targetViewModel: TargetViewModel = viewModel(key = "vm_${viewModel.viewModelStoreId}")
        targetViewModel.init(
            getAllPackagesUseCase = GetAllPackagesUseCase(packagesRepository),
            getLocalizedPackageNameUseCase = GetLocalizedPackageNameUseCase(packagesRepository),
            getPackageIconUseCase = GetPackageIconUseCase(packagesRepository),
            packageFingerprintService = AndroidPackageFingerprintService(LocalContext.current.packageManager),
            targets = viewModel.targets
        )
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

    //Dialog to create a detail:
    if (viewModel.isDetailDialogVisible) {
        val detailViewModel: DetailViewModel = viewModel(key = "vm_${viewModel.viewModelStoreId}")
        detailViewModel.init(
            detail = null
        )
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

    //Dialog to edit a detail:
    if (viewModel.detailToEdit != null) {
        val detailViewModel: DetailViewModel = viewModel(key = "vm_${viewModel.viewModelStoreId}")
        detailViewModel.init(
            detail = viewModel.detailToEdit
        )
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


@Composable
private fun DefaultAppBar(
    name: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigateUp: () -> Unit,
    onEditName: () -> Unit,
    onReorderDetails: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = name.ifEmpty { stringResource(R.string.account_namePlaceholder) },
                color = if (!name.isEmpty()) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(0.5f)
                },
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
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
        },
        actions = {
            IconButton(
                onClick = onReorderDetails
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_reorder),
                    contentDescription = ""
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}


@Composable
private fun ReorderAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onFinishReordering: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.account_titleReorder),
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onFinishReordering
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cancel),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}


/**
 * Displays the general section at the top of the page.
 *
 * @param description       Description of the account.
 * @param name              Name of the account.
 * @param tags              List of tags of the account.
 * @param onEditDescription Callback invoked to edit the description.
 * @param onEditTags        Callback invoked to edit the tags.
 * @param onEditTargets     Callback invoked to edit the targets.
 * @param onSave            Callback invoked to save the changes.
 * @param modifier          Modifier.
 */
@Composable
private fun GeneralSection(
    description: String,
    name: String,
    tags: List<TagUiDto>,
    onEditDescription: () -> Unit,
    onEditTags: () -> Unit,
    onEditTargets: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.padding_vertical))
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = description.ifEmpty { stringResource(R.string.account_descriptionPlaceholder) },
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
                    horizontal = dimensionResource(R.dimen.margin_horizontal)
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
 * @param isInReorderableState  Whether the screen is currently in reorder state.
 * @param onEdit                Callback invoked to edit the detail.
 * @param onDelete              Callback invoked to delete the detail.
 * @param onCopyToClipboard     Callback invoked to copy the detail content to the clipboard.
 * @param onReorderDetails      Callback invoked to begin reordering the details.
 * @param modifier              Modifier.
 */
@Composable
private fun DetailListRow(
    detail: Detail,
    isInReorderableState: Boolean,
    onEdit: (Detail) -> Unit,
    onDelete: (Detail) -> Unit,
    onCopyToClipboard: (Detail) -> Unit,
    onReorderDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isObfuscated: Boolean by remember { mutableStateOf(detail.metadata.isObfuscated) }
    var isDropdownVisible: Boolean by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(!isInReorderableState) {
                onEdit(detail)
            }
            .background(MaterialTheme.colorScheme.surface)
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
                text = if (detail.metadata.isObfuscated && isObfuscated) { stringResource(R.string.placeholder_obfuscated) } else { detail.content },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (isInReorderableState) {
            Icon(
                painter = painterResource(R.drawable.ic_draghandle),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 12.dp)
            )
        }
        else {
            Row(
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
                                Text(stringResource(R.string.account_details_edit))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_edit),
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                isDropdownVisible = false
                                onEdit(detail)
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.account_details_delete))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete),
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                isDropdownVisible = false
                                onDelete(detail)
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.account_details_copyToClipboard))
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
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.account_details_reorder))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_reorder),
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                isDropdownVisible = false
                                onReorderDetails()
                            }
                        )
                        if (detail.metadata.isObfuscated) {
                            DropdownMenuItem(
                                text = {
                                    Text(if (isObfuscated) {
                                        stringResource(R.string.account_details_showContent)
                                    } else {
                                        stringResource(R.string.account_details_hideContent)
                                    })
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = if (isObfuscated) {
                                            painterResource(R.drawable.ic_visibility_on)
                                        } else {
                                            painterResource(R.drawable.ic_visibility_off)
                                        },
                                        contentDescription = ""
                                    )
                                },
                                onClick = {
                                    isDropdownVisible = false
                                    isObfuscated = !isObfuscated
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
