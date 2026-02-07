package de.christian2003.core.ui.composables.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.christian2003.core.ui.R


/**
 * Displays a dialog with a hero section.
 *
 * @param title                 Title for the dialog.
 * @param text                  Text for the dialog.
 * @param onDismiss             Callback invoked to dismiss the dialog.
 * @param dismissButtonText     Text for the dismiss button
 * @param onConfirm             Optional callback to confirm an action.
 * @param confirmButtonText     Text for the confirm button.
 * @param heroSectionContent    Content for the hero section (e.g. an image).
 */
@Composable
fun DialogWithHeroSection(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    dismissButtonText: String = stringResource(R.string.button_dismiss),
    onConfirm: (() -> Unit)? = null,
    confirmButtonText: String = stringResource(R.string.button_confirm),
    heroSectionContent: @Composable () -> Unit
) {
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
                //Hero section:
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
                    heroSectionContent()
                }

                HorizontalDivider()

                //Scrollable content
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    )
                )
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                //Button row:
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp,
                            bottom = 24.dp
                        )
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(dismissButtonText)
                    }
                    if (onConfirm != null) {
                        TextButton(
                            onClick = onConfirm,
                            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                        ) {
                            Text(confirmButtonText)
                        }
                    }
                }
            }
        }
    }
}
