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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.HeadlineIndentation
import de.christian2003.core.ui.composables.TextInput
import de.christian2003.core.ui.model.InputError
import de.christian2003.feature.export.R
import de.christian2003.feature.export.domain.entities.ExportDescriptor


@Composable
internal fun ConfigurationView(
    descriptor: ExportDescriptor,
    directoryUri: Uri?,
    fileName: String,
    password: String,
    repeatPassword: String,
    directoryUriError: InputError?,
    fileNameError: InputError?,
    passwordError: InputError?,
    repeatPasswordError: InputError?,
    onDirectoryUriChange: (Uri) -> Unit,
    onFileNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Headline(
            title = stringResource(R.string.export_title_location),
            indentation = HeadlineIndentation.TextInputPrefixIconLevel
        )

        //Directory:
        DirectorySelector(
            dirUri = directoryUri,
            onDirUriChange = {
                onDirectoryUriChange(it)
            },
            errorMessage = directoryUriError?.message(),
            modifier = Modifier.padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
        )

        //Filename:
        TextInput(
            value = fileName,
            onValueChange = {
                onFileNameChange(it)
            },
            label = stringResource(R.string.export_label_filename),
            prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_file),
            errorMessage = fileNameError?.message(),
            modifier = Modifier.padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
        )

        //Password for encryption:
        if (descriptor.isExportEncrypted) {
            Headline(
                title = stringResource(R.string.export_title_encryption),
                indentation = HeadlineIndentation.TextInputPrefixIconLevel
            )

            TextInput(
                value = password,
                onValueChange = {
                    onPasswordChange(it)
                },
                label = stringResource(R.string.export_label_password),
                prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_password),
                errorMessage = passwordError?.message(),
                isPassword = true,
                modifier = Modifier.padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
            )

            TextInput(
                value = repeatPassword,
                onValueChange = {
                    onRepeatPasswordChange(it)
                },
                label = stringResource(R.string.export_label_password_repeat),
                errorMessage = repeatPasswordError?.message(),
                isPassword = true,
                indentToPrefixIcon = true,
                modifier = Modifier.padding(
                    start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                    end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                    bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
                )
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
