package de.christian2003.feature.files.ui.directory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.ListItemContainer
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.Shape
import de.christian2003.core.ui.composables.dialog.EditValueDialog
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.feature.files.viewmodels.DirectoryViewModel
import de.christian2003.feature.files.R
import de.christian2003.feature.files.models.dialog.DirectoryScreenDialog


@Composable
internal fun DirectoryScreen(
    viewModel: DirectoryViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToDirectory: (String) -> Unit
) {
    val subDirectories: List<InternalDirectory> by viewModel.subDirectories.collectAsState(emptyList())

    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp,
                onCreateDirectory = {
                    viewModel.dialog = DirectoryScreenDialog.CreateSubDirectory
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            LazyColumn {
                itemsIndexed(subDirectories) { index, internalDirectory ->
                    DirectoryListItem(
                        internalDirectory = internalDirectory,
                        isFirst = index == 0,
                        isLast = index == subDirectories.size - 1,
                        onClick = { directory ->
                            onNavigateToDirectory(directory.internalPath)
                        },
                        onRename = { directory ->

                        },
                        onDelete = { directory ->
                            viewModel.deleteDirectory(directory)
                        }
                    )
                }
                item {
                    Box(modifier = Modifier.height(bottomPadding))
                }
            }
        }

        NavigationBarProtection(bottomPadding)
    }

    when (viewModel.dialog) {
        DirectoryScreenDialog.CreateSubDirectory -> {
            EditValueDialog(
                value = "",
                onValidateValue = {
                    null //TODO: Dir name validation
                },
                label = "DIRECTORY NAME",
                title = "CREATE DIRECTORY",
                onDismiss = {
                    viewModel.dismissCreateDirectoryDialog()
                },
                onSave = { directoryName ->
                    viewModel.dismissCreateDirectoryDialog(directoryName)
                }
            )
        }
        else -> { }
    }
}


@Composable
private fun DirectoryListItem(
    internalDirectory: InternalDirectory,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: (InternalDirectory) -> Unit,
    onDelete: (InternalDirectory) -> Unit,
    onRename: (InternalDirectory) -> Unit
) {
    var isDropdownExpanded: Boolean by remember { mutableStateOf(false) }

    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(internalDirectory)
                }
                .padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal) - 12.dp,
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
        ) {
            Shape(
                shape = MaterialShapes.Cookie4Sided,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_m))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_directory),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Text(
                text = internalDirectory.internalName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal)
                    )
            )

            //Dropdown:
            Box {
                IconButton(
                    onClick = {
                        isDropdownExpanded = !isDropdownExpanded
                    }
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_more),
                        contentDescription = ""
                    )
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = {
                            isDropdownExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.directory_rename))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_edit),
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                onRename(internalDirectory)
                                isDropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.directory_delete))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                onDelete(internalDirectory)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun TopBar(
    onNavigateUp: () -> Unit,
    onCreateDirectory: () -> Unit
) {
    TopAppBar(
        title = {
            Text("DIRECTORY")
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
        },
        actions = {
            IconButton(
                onClick = onCreateDirectory
            ) {
                Icon(
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_add),
                    contentDescription = ""
                )
            }
        }
    )
}
