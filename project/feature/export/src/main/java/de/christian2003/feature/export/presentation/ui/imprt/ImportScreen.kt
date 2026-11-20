package de.christian2003.feature.export.presentation.ui.imprt

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.christian2003.feature.export.R
import de.christian2003.feature.export.domain.entities.ProgressState
import de.christian2003.feature.export.domain.entities.ServiceProgress
import de.christian2003.feature.export.presentation.ui.common.ProgressView
import de.christian2003.feature.export.presentation.viewmodels.ImportViewModel


/**
 * Displays the screen through which users can configure and import a file.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback to navigate up the nav stack.
 */
@Composable
internal fun ImportScreen(
    viewModel: ImportViewModel,
    onNavigateUp: () -> Unit
) {
    //Launcher shows explorer through which users can select a file to import:
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { result ->
        if (result != null) {
            viewModel.updateImportFileUri(result)
        }
    }

    //Launches the 'importLauncher':
    val launchImportLauncher: () -> Unit = {
        importLauncher.launch(viewModel.mimeTypeFilter)
    }


    //Composable content:

    LaunchedEffect(viewModel.importFileUri) {
        if (viewModel.importFileUri == null) {
            launchImportLauncher()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(viewModel.importServiceDescriptor.titleId),
                onNavigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomBar(
                canStartImport = viewModel.canStartImport.value,
                onImport = {
                    viewModel.import()
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        val progress: ServiceProgress? = viewModel.importProgress

        if (progress == null || (progress.state == ProgressState.None || progress.state == ProgressState.Finished || progress.state == ProgressState.Failed)) {
            //Displayed to configure import:
            ImportConfigurationView(
                fileName = viewModel.importFileName.value,
                importOption = viewModel.importOption,
                password = viewModel.password,
                isEncrypted = viewModel.isEncrypted.value,
                fileError = viewModel.importFileUriError,
                passwordError = viewModel.passwordError,
                onSelectFile = launchImportLauncher,
                onImportOptionSelected = { importOption ->
                    viewModel.importOption = importOption
                },
                onPasswordChange = { password ->
                    viewModel.password = password
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
        else {
            //Displayed during active import:
            ProgressView(
                progress = progress,
                onNavigateUp = onNavigateUp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}


/**
 * Top app bar displayed at the top of the screen.
 *
 * @param title         Title for the screen.
 * @param onNavigateUp  Callback to navigate up the nav stack.
 */
@Composable
private fun TopBar(
    title: String,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
        }
    )
}


/**
 * Bottom app bar displayed at the bottom of the screen.
 *
 * @param canStartImport    Whether the user may try to start an import at the moment.
 * @param onImport          Callback to try to start an import.
 */
@Composable
private fun BottomBar(
    canStartImport: Boolean,
    onImport: () -> Unit
) {
    BottomAppBar {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onImport,
                enabled = canStartImport,
                modifier = Modifier.padding(
                    //Horizontal padding of bottom app bar: 4 dp
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal) - 4.dp
                )
            ) {
                Text(stringResource(R.string.import_startImport))
            }
        }
    }
}
