package de.christian2003.filesysteminspector.ui.view

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import de.christian2003.filesysteminspector.R
import de.christian2003.filesysteminspector.domain.FileMetadata
import de.christian2003.filesysteminspector.ui.viewmodel.MainViewModel


@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToPackage: (String) -> Unit
) {
    val context: Context = LocalContext.current
    val currentDirectory: DocumentFile? = viewModel.directoryStack.lastOrNull()

    val directoryPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            viewModel.rootUri = uri
            viewModel.directoryStack.clear()
            viewModel.addUriToStack(uri)
        }
    }

    BackHandler(viewModel.directoryStack.size > 1) {
        viewModel.navigateUpDirectory()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (viewModel.directoryStack.size > 1) {
                        IconButton(
                            onClick = {
                                viewModel.navigateUpDirectory()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = viewModel.currentDirectoryName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentDirectory == null) {
                //No directory selected:
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            directoryPicker.launch(null)
                        }
                    ) {
                        Text("Choose Directory")
                    }
                }
            }
            else {
                //Directory selected:
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(viewModel.files) { file ->
                        FileItem(
                            file = file,
                            onClick = { documentFile ->
                                if (documentFile.isDirectory) {
                                    viewModel.directoryStack.add(documentFile)
                                }
                                else {
                                    viewModel.selectFile(documentFile)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    val selectedFile: FileMetadata? = viewModel.selectedFile
    if (selectedFile != null) {
        FileDetailsDialog(
            file = selectedFile,
            onDismiss = {
                viewModel.selectedFile = null
            }
        )
    }
}


@Composable
private fun FileItem(
    file: DocumentFile,
    onClick: (DocumentFile) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(file)
            }
            .padding(
                horizontal = 24.dp,
                vertical = 12.dp
            )
    ) {
        Icon(
            painter = if (file.isDirectory) {
                painterResource(R.drawable.ic_directory)
            } else {
                painterResource(R.drawable.ic_file)
            },
            contentDescription = "",
            tint = if (file.isDirectory) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.padding(end = 12.dp)
        )
        Text(
            text = file.name ?: "Unknown",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis
        )
    }
}
