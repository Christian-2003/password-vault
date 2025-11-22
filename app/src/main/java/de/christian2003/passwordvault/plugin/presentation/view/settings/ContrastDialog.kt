package de.christian2003.passwordvault.plugin.presentation.view.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.christian2003.passwordvault.R
import de.christian2003.passwordvault.plugin.presentation.ui.theme.PasswordVaultTheme
import de.christian2003.passwordvault.plugin.presentation.ui.theme.ThemeContrast


/**
 * Dialog through which the user can select a theme contrast.
 *
 * @param contrast  Contrast that is currently selected.
 * @param onDismiss Callback invoked to dismiss the dialog without saving.
 * @param onSave    Callback invoked to dismiss the dialog and save a theme.
 */
@Composable
fun ContrastDialog(
    contrast: ThemeContrast,
    onDismiss: () -> Unit,
    onSave: (ThemeContrast) -> Unit
) {
    var mutableContrast: ThemeContrast by rememberSaveable { mutableStateOf(contrast) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                //This material theme applies the selected contrast to the preview:
                PasswordVaultTheme(
                    contrast = mutableContrast
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(
                                start = 24.dp,
                                top = 24.dp,
                                end = 24.dp,
                                bottom = 16.dp
                            )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.settings_customization_contrast_dialogPreview_title),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                            )
                            Row {
                                val firstChar: Char? = stringResource(R.string.settings_customization_contrast_dialogPreview_itemTitle).firstOrNull { !it.isWhitespace() }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(dimensionResource(R.dimen.image_m))
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                ) {
                                    Text(
                                        text = firstChar?.toString() ?: "",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
                                ) {
                                    Text(
                                        text = stringResource(R.string.settings_customization_contrast_dialogPreview_itemTitle),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = stringResource(R.string.settings_customization_contrast_dialogPreview_itemText),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider()

                Text(
                    text = stringResource(R.string.settings_customization_contrast_dialogTitle),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    )
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    ThemeContrast.entries.forEach { contrast ->
                        SegmentedButton(
                            selected = contrast == mutableContrast,
                            onClick = {
                                mutableContrast = contrast
                            },
                            shape = RoundedCornerShape(
                                topStart = if (contrast.ordinal == 0) { 100.dp } else { 0.dp },
                                topEnd = if (contrast.ordinal == ThemeContrast.entries.size - 1) { 100.dp } else { 0.dp },
                                bottomStart = if (contrast.ordinal == 0) { 100.dp } else { 0.dp },
                                bottomEnd = if (contrast.ordinal == ThemeContrast.entries.size - 1) { 100.dp } else { 0.dp }
                            )
                        ) {
                            Text(
                                text = when(contrast) {
                                    ThemeContrast.Normal -> stringResource(R.string.settings_customization_contrast_dialogItemNormal)
                                    ThemeContrast.Medium -> stringResource(R.string.settings_customization_contrast_dialogItemMedium)
                                    ThemeContrast.High -> stringResource(R.string.settings_customization_contrast_dialogItemHigh)
                                }
                            )
                        }
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            horizontal = 24.dp,
                            vertical = 16.dp,
                        )
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                    TextButton(
                        onClick = {
                            onSave(mutableContrast)
                        },
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                    ) {
                        Text(stringResource(R.string.button_save))
                    }
                }
            }
        }
    }
}
