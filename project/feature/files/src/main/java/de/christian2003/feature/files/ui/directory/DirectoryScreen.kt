package de.christian2003.feature.files.ui.directory

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    val files: List<String> by viewModel.files.collectAsState(emptyList())

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.importFile(result)
    }

    Scaffold(
        topBar = {
            TopBar(
                directoryName = viewModel.directory.internalName,
                onNavigateUp = onNavigateUp,
                onCreateDirectory = {
                    viewModel.createNewDirectory()
                },
                onImportFile = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("*/*")
                    importLauncher.launch(intent)
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
                            viewModel.editDirectory(directory)
                        },
                        onDelete = { directory ->
                            viewModel.deleteDirectory(directory)
                        }
                    )
                }
                itemsIndexed(files) { index, file ->
                    FileListItem(
                        fileName = file,
                        isFirst = index == 0,
                        isLast = index == files.size - 1
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
        DirectoryScreenDialog.CreateSubDirectory, DirectoryScreenDialog.EditSubDirectory -> {
            val directoryToEdit: InternalDirectory? = viewModel.directoryToEdit
            val errorBlankInput: String = stringResource(de.christian2003.core.ui.R.string.error_blankInput)
            val errorIllegalChars: String = stringResource(de.christian2003.core.ui.R.string.error_illegalCharacters)

            EditValueDialog(
                value = directoryToEdit?.internalName ?: "",
                onValidateValue = { directoryName ->
                    val isValid: Boolean = viewModel.isDirectoryNameValid(directoryName)
                    when {
                        !isValid && directoryName.isBlank() -> errorBlankInput
                        !isValid -> errorIllegalChars
                        else -> null
                    }
                },
                label = stringResource(R.string.directory_label_directoryName),
                title = if (directoryToEdit == null) {
                    stringResource(R.string.directory_createNewDirectory)
                } else {
                    stringResource(R.string.directory_rename)
                },
                onDismiss = {
                    viewModel.dismissEditDirectoryDialog()
                },
                onSave = { directoryName ->
                    viewModel.dismissEditDirectoryDialog(directoryName)
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
private fun FileListItem(
    fileName: String,
    isFirst: Boolean,
    isLast: Boolean
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
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
                    painter = painterResource(R.drawable.ic_file_generic),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(dimensionResource(de.christian2003.core.ui.R.dimen.image_xs))
                )
            }
            Text(
                text = fileName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal)
                    )
            )

            //Dropdown:
            //TODO
        }
    }
}


@Composable
private fun TopBar(
    directoryName: String,
    onNavigateUp: () -> Unit,
    onCreateDirectory: () -> Unit,
    onImportFile: () -> Unit
) {
    var isDropdownExpanded: Boolean by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(
                text = directoryName.ifBlank { stringResource(R.string.directory_title) }
            )
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
                                Text(stringResource(R.string.directory_createNewDirectory))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add_directory),
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                onCreateDirectory()
                                isDropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.directory_file_import))
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_add_file),
                                    contentDescription = ""
                                )
                            },
                            onClick = {
                                onImportFile()
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}
