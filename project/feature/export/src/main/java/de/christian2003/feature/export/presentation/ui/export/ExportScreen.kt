package de.christian2003.feature.export.presentation.ui.export

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import de.christian2003.core.ui.composables.NavigationBarProtection
import de.christian2003.core.ui.composables.TextInput
import de.christian2003.feature.export.presentation.viewmodels.ExportViewModel
import de.christian2003.feature.export.R


@Composable
internal fun ExportScreen(
    viewModel: ExportViewModel,
    onNavigateUp: () -> Unit
) {
    val fileSelectorLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data?.data != null) {
            viewModel.uri = result.data?.data
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateUp = onNavigateUp
            )
        }
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
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("application/zip")
                    intent.putExtra(Intent.EXTRA_TITLE, "export.zip")
                    fileSelectorLauncher.launch(intent)
                }
            ) {
                Text("SELECT FILE")
            }
            Text(
                text = viewModel.uri?.toString() ?: "No file selected"
            )

            TextInput(
                value = viewModel.password,
                onValueChange = {
                    viewModel.password = it
                },
                label = "Password",
                isPassword = true
            )

            Button(
                enabled = viewModel.uri != null && viewModel.password.isNotBlank(),
                onClick = {
                    viewModel.export()
                }
            ) {
                Text("EXPORT")
            }
            LinearProgressIndicator(
                progress = { viewModel.exportProgress },
                modifier = Modifier.fillMaxWidth()
            )
            Box(modifier = Modifier.height(bottomPadding))
        }

        NavigationBarProtection(bottomPadding)
    }
}


@Composable
private fun TopBar(
    onNavigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.export_title))
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
