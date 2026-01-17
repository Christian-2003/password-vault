package de.christian2003.ui.composables

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
import de.christian2003.ui.R


/**
 * Displays a simple dialog with actions.
 *
 * @param title             Title for the dialog.
 * @param text              Text for the dialog.
 * @param onDismiss         Callback invoked to dismiss the dialog.
 * @param onConfirm         Callback invoked to confirm an action.
 * @param dismissButtonText Text for the button to dismiss the dialog.
 * @param confirmButtonText Text for the button to confirm an action.
 */
@Composable
fun SimpleDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null,
    dismissButtonText: String = stringResource(R.string.button_dismiss),
    confirmButtonText: String = stringResource(R.string.button_confirm)
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
                    .padding(24.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                //Button row:
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.align(Alignment.End)
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
