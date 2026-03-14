package de.christian2003.feature.files.ui.directory

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.dialog.EditValueDialog
import de.christian2003.data.files.domain.entities.InternalDirectory
import de.christian2003.feature.files.viewmodels.DirectoryViewModel
import de.christian2003.feature.files.R
import de.christian2003.feature.files.models.dialog.DirectoryScreenDialog


@Composable
internal fun DirectoryScreen(
    viewModel: DirectoryViewModel,
    onNavigateUp: () -> Unit
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
                    Row {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                                )
                        ) {
                            Text(
                                text = internalDirectory.internalName,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = internalDirectory.internalPath,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.deleteDirectory(internalDirectory)
                            }
                        ) {
                            Icon(
                                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_delete),
                                contentDescription = ""
                            )
                        }
                    }

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
