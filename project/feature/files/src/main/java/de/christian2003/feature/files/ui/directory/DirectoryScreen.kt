package de.christian2003.feature.files.ui.directory

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.EmptyPlaceholder
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.Tooltip
import de.christian2003.core.ui.composables.dialog.ConfirmDeleteDialog
import de.christian2003.core.ui.composables.dialog.EditValueDialog
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.SharedFile
import de.christian2003.feature.files.viewmodels.DirectoryViewModel
import de.christian2003.feature.files.R
import de.christian2003.feature.files.models.dialog.DirectoryScreenDialog
import de.christian2003.feature.files.models.states.DirectoryScreenState
import de.christian2003.feature.files.ui.breadcrumbs.Breadcrumb
import de.christian2003.feature.files.ui.breadcrumbs.Breadcrumbs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
internal fun DirectoryScreen(
    viewModel: DirectoryViewModel,
    onNavigateUp: () -> Unit
) {
    val subDirectories: List<InternalDirectory> by viewModel.subDirectories.collectAsState(emptyList())
    val files: List<InternalFile> by viewModel.files.collectAsState(emptyList())

    val context: Context = LocalContext.current
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.importFile(result)
    }

    val invokeOnNavigateUp: () -> Unit = {
        when (viewModel.state) {
            DirectoryScreenState.Default -> {
                if (!viewModel.navigateUp()) {
                    //Top directory reached:
                    onNavigateUp()
                }
            }
            DirectoryScreenState.Multiselect -> viewModel.dismissMultiselect()
        }
    }

    val onImportFile: () -> Unit = {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("*/*")
        importLauncher.launch(intent)
    }

    BackHandler {
        invokeOnNavigateUp()
    }

    Scaffold(
        topBar = {
            TopBar(
                state = viewModel.state,
                directoryName = viewModel.directory.internalName,
                selectedFileCount = viewModel.selectedFiles.size,
                selectedDirectoryCount = viewModel.selectedSubdirectories.size,
                onNavigateUp = invokeOnNavigateUp,
                onSelectAll = {
                    viewModel.multiselectSelectAll(subDirectories, files)
                },
                onCreateDirectory = {
                    viewModel.createNewDirectory()
                },
                onImportFile = onImportFile
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
            DirectoryBreadcrumbs(
                state = viewModel.state,
                directory = viewModel.directory,
                onNavigateUpToDirectory = { directory ->
                    viewModel.navigateUpToDirectory(directory)
                }
            )

            if (subDirectories.isEmpty() && files.isEmpty()) {
                EmptyPlaceholder(
                    title = stringResource(R.string.directory_emptyPlaceholder_title),
                    subtitle = stringResource(R.string.directory_emptyPlaceholder_subtitle),
                    painter = painterResource(R.drawable.el_directory),
                    onButtonClick = onImportFile,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(stringResource(R.string.directory_file_import))
                }
            }
            else {
                LazyColumn {
                    itemsIndexed(subDirectories) { index, internalDirectory ->
                        DirectoryListItem(
                            state = viewModel.state,
                            internalDirectory = internalDirectory,
                            isFirst = index == 0,
                            isLast = index == subDirectories.size - 1,
                            isSelected = viewModel.selectedSubdirectories.contains(internalDirectory.internalName),
                            onClick = { directory ->
                                viewModel.navigateToDirectory(directory)
                            },
                            onRename = { directory ->
                                viewModel.editDirectory(directory)
                            },
                            onDelete = { directory ->
                                viewModel.deleteDirectory(directory)
                            },
                            onToggleSelected = { directory ->
                                viewModel.multiselectToggleDirectorySelected(directory)
                            },
                            onStartMultiselect = { directory ->
                                viewModel.startMultiselect(directory)
                            }
                        )
                    }
                    itemsIndexed(files) { index, file ->
                        FileListItem(
                            state = viewModel.state,
                            internalFile = file,
                            isFirst = index == 0,
                            isLast = index == files.size - 1,
                            isSelected = viewModel.selectedFiles.contains(file.internalName),
                            onFormatStorageSize = {
                                viewModel.formatBytes(it)
                            },
                            onFormatDateTime = {
                                viewModel.formateDateTime(it)
                            },
                            onQueryFileType = { mimeType ->
                                viewModel.queryFileType(mimeType)
                            },
                            onDelete = { file ->
                                viewModel.deleteFile(file)
                            },
                            onRename = { file ->
                                viewModel.renameFile(file)
                            },
                            onOpenWith = { file ->
                                coroutineScope.launch(Dispatchers.IO) {
                                    val sharedFile: SharedFile? = viewModel.prepareFileForViewing(file)
                                    if (sharedFile != null) {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(sharedFile.contentUri, sharedFile.mimeType)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        val chooser = Intent.createChooser(intent, context.getString(R.string.directory_file_openWith))
                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            withContext(Dispatchers.Main) {
                                                context.startActivity(chooser)
                                            }
                                        }
                                    }
                                }
                            },
                            onMoreInfo = { file ->
                                viewModel.moreInfoForFile(file)
                            },
                            onStartMultiselect = { file ->
                                viewModel.startMultiselect(file = file)
                            },
                            onToggleSelected = { file ->
                                viewModel.multiselectToggleFileSelected(file)
                            }

                        )
                    }
                    item {
                        Box(modifier = Modifier.height(bottomPadding))
                    }
                }
            }
        }

        if (viewModel.state == DirectoryScreenState.Multiselect) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = bottomPadding + dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                    )
            ) {
                BottomBar(
                    state = viewModel.state
                )
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
        DirectoryScreenDialog.ConfirmDeleteDirectory -> {
            val directoryToDelete: InternalDirectory? = viewModel.directoryToDelete
            if (directoryToDelete != null) {
                ConfirmDeleteDialog(
                    text = stringResource(R.string.directory_confirmDelete, directoryToDelete.internalName),
                    onDismiss = {
                        viewModel.dismissDeleteDirectoryDialog(false)
                    },
                    onConfirm = {
                        viewModel.dismissDeleteDirectoryDialog(true)
                    }
                )
            }
        }
        DirectoryScreenDialog.ConfirmDeleteFile -> {
            val fileToDelete: InternalFile? = viewModel.fileToDelete
            if (fileToDelete != null) {
                ConfirmDeleteDialog(
                    text = stringResource(R.string.directory_file_confirmDelete, fileToDelete.actualFileName),
                    onDismiss = {
                        viewModel.dismissConfirmDeleteFileDialog(false)
                    },
                    onConfirm = {
                        viewModel.dismissConfirmDeleteFileDialog(true)
                    }
                )
            }
        }
        DirectoryScreenDialog.RenameFile -> {
            val fileToEdit: InternalFile? = viewModel.fileToEdit
            val errorBlankInput: String = stringResource(de.christian2003.core.ui.R.string.error_blankInput)
            val errorIllegalChars: String = stringResource(de.christian2003.core.ui.R.string.error_illegalCharacters)

            if (fileToEdit != null) {
                EditValueDialog(
                    value = fileToEdit.actualFileName,
                    onValidateValue = { fileName ->
                        val isValid: Boolean = viewModel.isFileNameValid(fileName)
                        when {
                            !isValid && fileName.isBlank() -> errorBlankInput
                            !isValid -> errorIllegalChars
                            else -> null
                        }
                    },
                    label = stringResource(R.string.directory_label_fileName),
                    title = stringResource(R.string.directory_file_rename),
                    onDismiss = {
                        viewModel.dismissRenameFileDialog(null)
                    },
                    onSave = { fileName ->
                        viewModel.dismissRenameFileDialog(fileName)
                    }
                )
            }
        }
        DirectoryScreenDialog.FileDetails -> {
            val fileForDetails: InternalFile? = viewModel.fileForDetails
            if (fileForDetails != null) {
                val isShared: Boolean = remember(fileForDetails) { viewModel.isFileShared(fileForDetails) }
                FileDetailSheet(
                    file = fileForDetails,
                    directory = viewModel.directory,
                    isShared = isShared,
                    bottomPadding = 0.dp,
                    onQueryFileType = { mimeType ->
                        viewModel.queryFileType(mimeType)
                    },
                    onFormatStorageUnit = { bytes ->
                        viewModel.formatBytes(bytes)
                    },
                    onFormatTime = { time ->
                        viewModel.formateDateTime(time)
                    },
                    onGeneratePositiveColor = { negativeColor, darkTheme ->
                        viewModel.generatePositiveColor(negativeColor, darkTheme)
                    },
                    onDismiss = {
                        viewModel.dismissFileDetailsDialog()
                    }
                )
            }
        }
        else -> { }
    }
}


@Composable
private fun DirectoryBreadcrumbs(
    state: DirectoryScreenState,
    directory: InternalDirectory,
    onNavigateUpToDirectory: (InternalDirectory) -> Unit
) {
    val parts: List<String> = directory.internalPath.split('/')
    val breadcrumbs: MutableList<Breadcrumb> = mutableListOf()

    val onInvokeNavigateUpToDirectory: (Int) -> Unit = { breadcrumbIndex ->
        if (breadcrumbIndex >= 0 && breadcrumbIndex < parts.size) {
            val directoryPath: String = parts.take(breadcrumbIndex).joinToString("/")
            val newDirectory = InternalDirectory(directoryPath)
            onNavigateUpToDirectory(newDirectory)
        }
    }

    val homeBreadcrumb = Breadcrumb(
        label = stringResource(R.string.directory_title),
        onClick = if (state == DirectoryScreenState.Default && parts.isNotEmpty() && parts[0].isNotEmpty()) {
                {
                    onInvokeNavigateUpToDirectory(0)
                }
            }
            else {
                null
            }
    )
    breadcrumbs.add(homeBreadcrumb)

    parts.dropWhile {
        it == "" //Disregard top-level directory because it is represented through the home breadcrumb
    }.forEachIndexed { index, directory ->
        val breadcrumb = Breadcrumb(
            label = directory,
            onClick = if (state == DirectoryScreenState.Default && index != parts.size - 1) {
                {
                    onInvokeNavigateUpToDirectory(index + 1)
                }
            } else {
                null
            }
        )
        breadcrumbs.add(breadcrumb)
    }

    Breadcrumbs(
        items = breadcrumbs,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
private fun TopBar(
    state: DirectoryScreenState,
    directoryName: String,
    selectedFileCount: Int,
    selectedDirectoryCount: Int,
    onNavigateUp: () -> Unit,
    onSelectAll: () -> Unit,
    onCreateDirectory: () -> Unit,
    onImportFile: () -> Unit
) {
    when (state) {
        DirectoryScreenState.Default -> {
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
        DirectoryScreenState.Multiselect -> {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            selectedDirectoryCount == 0 -> pluralStringResource(R.plurals.directory_titleMultiselect_files, selectedFileCount, selectedFileCount)
                            selectedFileCount == 0 -> pluralStringResource(R.plurals.directory_titleMultiselect_directories, selectedDirectoryCount, selectedDirectoryCount)
                            else -> pluralStringResource(R.plurals.directory_titleMultiselect_filesAndDirectories, selectedFileCount + selectedDirectoryCount, selectedFileCount + selectedDirectoryCount)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_cancel),
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSelectAll
                    ) {
                        Icon(
                            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_selectall),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    }
}


@Composable
private fun BottomBar(
    state: DirectoryScreenState,
    modifier: Modifier = Modifier
) {
    if (state == DirectoryScreenState.Multiselect) {
        HorizontalFloatingToolbar(
            expanded = true,
            modifier = modifier
        ) {
            Tooltip(
                tooltip = stringResource(R.string.directory_multiselect_moveHint),
                anchor = TooltipAnchorPosition.Above
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_move),
                        contentDescription = ""
                    )
                }
            }

            Tooltip(
                tooltip = stringResource(R.string.directory_multiselect_copyHint),
                anchor = TooltipAnchorPosition.Above
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_copy),
                        contentDescription = ""
                    )
                }
            }

            Tooltip(
                tooltip = stringResource(R.string.directory_multiselect_shareHint),
                anchor = TooltipAnchorPosition.Above
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_share),
                        contentDescription = ""
                    )
                }
            }

            Tooltip(
                tooltip = stringResource(R.string.directory_multiselect_deleteHint),
                anchor = TooltipAnchorPosition.Above
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
                        contentDescription = ""
                    )
                }
            }
        }
    }
}
