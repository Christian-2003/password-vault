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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.plugin.presentation.ui.composables.ConfirmDeleteDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EditValueDialog
import de.christian2003.passwordvault.plugin.presentation.ui.composables.EmptyPlaceholder
import de.christian2003.passwordvault.plugin.presentation.ui.composables.Headline
import de.christian2003.passwordvault.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.passwordvault.plugin.presentation.view.detail.DetailSheet
import de.christian2003.passwordvault.plugin.presentation.view.detail.DetailViewModel
import de.christian2003.passwordvault.plugin.presentation.view.tag.TagSheet
import de.christian2003.passwordvault.plugin.presentation.view.tag.TagViewModel
import kotlin.text.ifEmpty


@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    onNavigateUp: () -> Unit
) {
    val appBarState: TopAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)
    val details: List<Detail> = viewModel.details

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = viewModel.name.ifEmpty { stringResource(R.string.account_namePlaceholder) },
                        color = if (!viewModel.name.isEmpty()) {
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
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
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
                items(details) { detail ->
                    DetailListRow(
                        detail = detail,
                        onEditDetail = {
                            viewModel.detailToEdit = it
                        },
                        onDeleteDetail = {
                            viewModel.detailToDelete = it
                        },
                        onCopyToClipboard = {
                            viewModel.copyToClipboard(it)
                        }
                    )
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

        if (viewModel.isNameDialogVisible) {
            EditValueDialog(
                value = viewModel.name,
                canValueBeBlank = false,
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

        if (viewModel.isDescriptionDialogVisible) {
            EditValueDialog(
                value = viewModel.description,
                canValueBeBlank = true,
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

    if (viewModel.isTagDialogVisible) {
        val tagViewModel: TagViewModel = viewModel(key = "vm_${viewModel.viewModelStoreId}")
        tagViewModel.init(
            tagRepository = viewModel.tagRepository,
            selectedTags = viewModel.tags
        )
        TagSheet(
            viewModel = tagViewModel,
            onDismiss = {
                viewModel.dismissTagDialog()
            },
            onSave = { selectedTags ->
                viewModel.dismissTagDialog(selectedTags)
            }
        )
    }

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
private fun GeneralSection(
    description: String,
    onEditDescription: () -> Unit,
    name: String,
    tags: List<Tag>,
    onEditTags: () -> Unit,
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
                    horizontal = dimensionResource(R.dimen.padding_horizontal)
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


@Composable
private fun DetailListRow(
    detail: Detail,
    onEditDetail: (Detail) -> Unit,
    onDeleteDetail: (Detail) -> Unit,
    onCopyToClipboard: (Detail) -> Unit
) {
    var isObfuscated: Boolean by remember { mutableStateOf(detail.metadata.isObfuscated) }
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
                text = if (detail.metadata.isObfuscated && isObfuscated) { stringResource(R.string.placeholder_obfuscated) } else { detail.content },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
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
                            onEditDetail(detail)
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
                            onDeleteDetail(detail)
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
