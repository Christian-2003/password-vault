package de.christian2003.feature.export.presentation.ui.export

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.TextInput
import de.christian2003.feature.export.presentation.viewmodels.ExportViewModel
import de.christian2003.feature.export.R


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
                onExport = {
                    viewModel.export()
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        val errorBlankInput: String = stringResource(de.christian2003.core.ui.R.string.error_blankInput)
        val errorIllegalFilename: String = stringResource(de.christian2003.core.ui.R.string.error_illegalFilename)
        val errorPasswordsNotMatching: String = stringResource(de.christian2003.core.ui.R.string.error_passwordsNotMatching)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                )
        ) {
            //Directory:
            DirectorySelector(
                dirUri = viewModel.directoryUri,
                onDirUriChange = {
                    viewModel.directoryUri = it
                },
                errorMessage = if (!viewModel.isDirectoryUriValid) { errorBlankInput } else { null },
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            //Filename:
            TextInput(
                value = viewModel.fileName,
                onValueChange = {
                    viewModel.fileName = it
                },
                label = stringResource(R.string.export_label_filename),
                prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_file),
                errorMessage = when {
                    !viewModel.isFileNameValid && viewModel.fileName.isBlank() -> errorBlankInput
                    !viewModel.isFileNameValid -> errorIllegalFilename
                    else -> null
                },
                modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )

            //Password for encryption:
            if (viewModel.exportServiceDescriptor.isExportEncrypted) {
                TextInput(
                    value = viewModel.password,
                    onValueChange = {
                        viewModel.password = it
                    },
                    label = stringResource(R.string.export_label_password),
                    prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_password),
                    errorMessage = if (!viewModel.isPasswordValid) { errorBlankInput } else { null },
                    isPassword = true,
                    modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                )

                TextInput(
                    value = viewModel.repeatPassword,
                    onValueChange = {
                        viewModel.repeatPassword = it
                    },
                    label = stringResource(R.string.export_label_password_repeat),
                    errorMessage = when {
                        !viewModel.isRepeatPasswordValid && viewModel.repeatPassword.isBlank() -> errorBlankInput
                        !viewModel.isRepeatPasswordValid -> errorPasswordsNotMatching
                        else -> null
                    },
                    isPassword = true,
                    indentToPrefixIcon = true,
                    modifier = Modifier.padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                )
            }

            LinearProgressIndicator(
                progress = { viewModel.exportProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )
        }
    }
}


@Composable
private fun DirectorySelector(
    dirUri: Uri?,
    onDirUriChange: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    val dirSelectorLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data?.data != null) {
            onDirUriChange(result.data!!.data!!)
        }
    }

    var uriPath: String = dirUri?.path ?: ""
    val schemeSeparator: Int = uriPath.indexOfFirst { it == ':' }
    if (schemeSeparator >= 0 && schemeSeparator < uriPath.length - 1) {
        uriPath = uriPath.drop(schemeSeparator + 1)
    }

    TextInput(
        value = uriPath,
        onValueChange = { },
        label = stringResource(R.string.export_label_dir),
        prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_directory),
        errorMessage = errorMessage,
        keyboardOptions = KeyboardOptions(showKeyboardOnFocus = false),
        modifier = modifier.pointerInput(null) {
            awaitEachGesture {
                awaitFirstDown(pass = PointerEventPass.Initial)
                val upEvent: PointerInputChange? = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                if (upEvent != null) {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    if (dirUri != null) {
                        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, dirUri)
                    }
                    dirSelectorLauncher.launch(intent)
                }
            }
        }
    )
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
    onExport: () -> Unit
) {
    BottomAppBar {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onExport,
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
