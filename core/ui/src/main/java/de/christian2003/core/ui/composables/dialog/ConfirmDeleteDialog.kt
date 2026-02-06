package de.christian2003.core.ui.composables.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.christian2003.core.ui.R


/**
 * Displays a dialog asking the user for confirmation to delete an item.
 *
 * @param text              Information text to display to the user.
 * @param onDismiss         Dismiss the dialog without deleting the item.
 * @param onConfirm         Delete the item and dismiss the dialog.
 * @param title             Title for the dialog.
 * @param dismissButtonText Text for the dismiss button.
 * @param confirmButtonText Text for the confirm button.
 */
@Composable
fun ConfirmDeleteDialog(
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String = stringResource(R.string.dialog_confirmDelete_title),
    dismissButtonText: String = stringResource(R.string.button_cancel),
    confirmButtonText: String = stringResource(R.string.button_delete)
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
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 24.dp
                    )
                )

                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(dismissButtonText)
                    }
                    TextButton(
                        onClick = onConfirm,
                        colors = ButtonDefaults.textButtonColors().copy(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(confirmButtonText)
                    }
                }
            }
        }
    }
}
