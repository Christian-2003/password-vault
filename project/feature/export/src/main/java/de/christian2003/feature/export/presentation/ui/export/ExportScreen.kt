package de.christian2003.feature.export.presentation.ui.export

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.dialog.AccountSelectorDialog
import de.christian2003.core.ui.model.AccountUiDto
import de.christian2003.feature.export.presentation.viewmodels.ExportViewModel
import de.christian2003.feature.export.R
import de.christian2003.feature.export.domain.entities.ExportProgress
import de.christian2003.feature.export.domain.entities.ProgressState
import de.christian2003.feature.export.presentation.model.dialogs.ExportScreenDialog


@Composable
internal fun ExportScreen(
    viewModel: ExportViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(viewModel.exportServiceDescriptor.titleId),
                onNavigateUp = onNavigateUp
            )
        },
        bottomBar = {
            BottomBar(
                canStartExport = viewModel.canStartExport.value,
                onExport = {
                    viewModel.export()
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        val progress: ExportProgress? = viewModel.exportProgress

        if (progress == null || (progress.state == ProgressState.None || progress.state == ProgressState.Finished || progress.state == ProgressState.Failed)) {
            ConfigurationView(
                descriptor = viewModel.exportServiceDescriptor,
                directoryUri = viewModel.directoryUri,
                fileName = viewModel.fileName,
                selectedAccountsCount = viewModel.selectedAccountIds.size,
                selectedFilesCount = 0, //TODO
                password = viewModel.password,
                repeatPassword = viewModel.repeatPassword,
                directoryUriError = viewModel.directoryUriError,
                fileNameError = viewModel.fileNameError,
                passwordError = viewModel.passwordError,
                repeatPasswordError = viewModel.repeatPasswordError,
                onDirectoryUriChange = {
                    viewModel.directoryUri = it
                },
                onFileNameChange = {
                    viewModel.fileName = it
                },
                onSelectAccounts = {
                    viewModel.showSelectAccountsDialog()
                },
                onSelectFiles = {
                    viewModel.showSelectFilesDialog()
                },
                onPasswordChange = {
                    viewModel.password = it
                },
                onRepeatPasswordChange = {
                    viewModel.repeatPassword = it
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
        else {
            ProgressView(
                progress = progress,
                onNavigateUp = onNavigateUp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }

    when (viewModel.dialog) {
        ExportScreenDialog.SelectAccounts -> {
            AccountSelectorDialog(
                title = stringResource(R.string.export_dialog_accounts_title),
                accounts = viewModel.accounts.map { accountDescriptor ->
                    AccountUiDto(
                        id = accountDescriptor.id,
                        name = accountDescriptor.name,
                        description = accountDescriptor.description,
                        icon = viewModel.queryAccountIcon(accountDescriptor)
                    )
                },
                selectedAccounts = viewModel.selectedAccountIds,
                onSave = { selectedAccountIds ->
                    viewModel.dismissSelectAccountsDialog(selectedAccountIds)
                },
                onDismiss = {
                    viewModel.dismissSelectAccountsDialog()
                }
            )
        }
        else -> { }
    }
}


@Composable
private fun TopBar(
    title: String,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(title)
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


@Composable
private fun BottomBar(
    canStartExport: Boolean,
    onExport: () -> Unit
) {
    BottomAppBar {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onExport,
                enabled = canStartExport,
                modifier = Modifier.padding(
                    //Horizontal padding of bottom app bar: 4 dp
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal) - 4.dp
                )
            ) {
                Text(stringResource(R.string.export_startExport))
            }
        }
    }
}
