package de.christian2003.feature.export.presentation.ui.imprt

import androidx.appcompat.widget.DialogTitle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.christian2003.feature.export.domain.entities.ProgressState
import de.christian2003.feature.export.domain.entities.ServiceProgress
import de.christian2003.feature.export.presentation.ui.common.ProgressView
import de.christian2003.feature.export.presentation.viewmodels.ImportViewModel


@Composable
internal fun ImportScreen(
    viewModel: ImportViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(viewModel.importServiceDescriptor.titleId),
                onNavigateUp = onNavigateUp
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        val progress: ServiceProgress? = viewModel.importProgress

        if (progress == null || (progress.state == ProgressState.None || progress.state == ProgressState.Finished || progress.state == ProgressState.Failed)) {
            ImportConfigurationView(
                fileUri = null,
                importOption = viewModel.importOption,
                password = viewModel.password,
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


@Composable
private fun TopBar(
    title: String,
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
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
