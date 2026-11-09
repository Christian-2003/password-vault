package de.christian2003.feature.export.presentation.ui.imprt

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.composables.Headline
import de.christian2003.core.ui.composables.HeadlineIndentation
import de.christian2003.core.ui.composables.TextInput
import de.christian2003.feature.export.R
import de.christian2003.feature.export.domain.entities.ImportOptions


@Composable
internal fun ImportConfigurationView(
    fileUri: Uri?,
    importOption: ImportOptions,
    password: String,
    onImportOptionSelected: (ImportOptions) -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        //Select file:
        Headline(
            title = stringResource(R.string.import_title_file),
            indentation = HeadlineIndentation.TextInputPrefixIconLevel
        )
        File(
            fileName = fileUri?.lastPathSegment ?: "",
            onChange = {
                //TODO: File selector
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal))
        )

        //Select import option:
        Headline(
            title = stringResource(R.string.import_title_chooseImportOption),
            indentation = HeadlineIndentation.TextInputPrefixIconLevel,
            modifier = Modifier.padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
        )
        Text(
            text = stringResource(R.string.import_text_chooseImportOption),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                        + 24.dp //Width of Radio buttons / icons
                        + dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
            )
        )
        ImportOptionSelector(
            importOption = importOption,
            onImportOptionSelected = onImportOptionSelected
        )

        //Enter encryption password:
        Headline(
            title = stringResource(R.string.import_title_encryption),
            indentation = HeadlineIndentation.TextInputPrefixIconLevel,
            modifier = Modifier.padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
        )
        Text(
            text = stringResource(R.string.import_text_encryption),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
                        + 24.dp //Width of Radio buttons / icons
                        + dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
            )
        )
        TextInput(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.import_label_password),
            prefixIcon = painterResource(de.christian2003.core.ui.R.drawable.ic_password),
            isPassword = true,
            modifier = Modifier.padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
            )
        )
    }
}


@Composable
private fun File(
    fileName: String?,
    onChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected: Boolean = !fileName.isNullOrBlank()
    Row(
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(de.christian2003.core.ui.R.drawable.ic_file),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal))
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable {
                    onChange()
                }
                .padding(
                    horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.padding_horizontal),
                    vertical = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                )
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.import_label_file),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (isSelected) {
                        fileName ?: ""
                    } else {
                        stringResource(R.string.import_text_noFileSelected)
                    },
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                painter = painterResource(de.christian2003.core.ui.R.drawable.ic_edit),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun ImportOptionSelector(
    importOption: ImportOptions,
    onImportOptionSelected: (ImportOptions) -> Unit
) {
    //Delete all existing data:
    ImportOptionSelectorItem(
        title = stringResource(R.string.import_option_deleteAllExistingData_title),
        info = stringResource(R.string.import_option_deleteAllExistingData_info),
        isSelected = importOption == ImportOptions.DeleteAllExistingData,
        onClick = {
            onImportOptionSelected(ImportOptions.DeleteAllExistingData)
        },
        modifier = Modifier.fillMaxWidth()
    )

    //Override duplicates:
    ImportOptionSelectorItem(
        title = stringResource(R.string.import_option_overrideDuplicates_title),
        info = stringResource(R.string.import_option_overrideDuplicates_info),
        isSelected = importOption == ImportOptions.OverrideDuplicates,
        onClick = {
            onImportOptionSelected(ImportOptions.OverrideDuplicates)
        },
        modifier = Modifier.fillMaxWidth()
    )

    //Skip duplicates:
    ImportOptionSelectorItem(
        title = stringResource(R.string.import_option_skipDuplicates_title),
        info = stringResource(R.string.import_option_skipDuplicates_info),
        isSelected = importOption == ImportOptions.SkipDuplicates,
        onClick = {
            onImportOptionSelected(ImportOptions.SkipDuplicates)
        },
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
private fun ImportOptionSelectorItem(
    title: String,
    info: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                onClick()
            }
            .padding(
                start = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal) - 12.dp,
                top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical),
                end = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal),
                bottom = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical)
            )
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )

        Column {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = info,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
