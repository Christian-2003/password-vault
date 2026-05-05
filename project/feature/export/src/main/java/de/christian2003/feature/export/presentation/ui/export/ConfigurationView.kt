package de.christian2003.feature.export.presentation.ui.export

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.resourceinspection.annotation.Attribute
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.HeadlineIndentation
import de.christian2003.core.ui.composables.TextInput
import de.christian2003.core.ui.model.InputError
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.feature.export.R
import de.christian2003.feature.export.domain.entities.ExportDescriptor
import kotlin.uuid.Uuid


@Composable
internal fun ConfigurationView(
    descriptor: ExportDescriptor,
    directoryUri: Uri?,
    fileName: String,
    selectedAccountsCount: Int,
    selectedFilesCount: Int,
    password: String,
    repeatPassword: String,
    directoryUriError: InputError?,
    fileNameError: InputError?,
    passwordError: InputError?,
    repeatPasswordError: InputError?,
    onDirectoryUriChange: (Uri) -> Unit,
    onSelectAccounts: () -> Unit,
    onSelectFiles: () -> Unit,
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
            indentToPrefixIcon = true,
            errorMessage = fileNameError?.message(),
            modifier = Modifier.padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
        )

        //Data selection:
        if (descriptor.canExportAccounts || descriptor.canExportFiles) {
            Headline(
                title = stringResource(R.string.export_title_data),
                indentation = HeadlineIndentation.TextInputPrefixIconLevel
            )
            if (descriptor.canExportAccounts) {
                DataSelectorItem(
                    label = stringResource(R.string.export_label_accounts),
                    text = pluralStringResource(R.plurals.export_text_accounts, selectedAccountsCount, selectedAccountsCount),
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_accounts),
                    onClick = onSelectAccounts
                )
            }
            if (descriptor.canExportFiles) {
                DataSelectorItem(
                    label = stringResource(R.string.export_label_files),
                    text = pluralStringResource(R.plurals.export_text_files, selectedFilesCount, selectedFilesCount),
                    painter = painterResource(de.christian2003.core.ui.R.drawable.ic_file),
                    onClick = onSelectFiles
                )
            }
        }

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
private fun DataSelectorItem(
    label: String,
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        Icon(
            painter = painter,
            contentDescription = "",
            modifier = Modifier.padding(end = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
        )
        Column {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = text,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
