package de.passwordvault.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.passwordvault.R
import kotlinx.coroutines.launch


/**
 * Composable displays a modal bottom sheet dialog through which the user can confirm a deletion.
 *
 * @param message   Message to display to the user.
 * @param onCancel  Callback to invoke if the deletion is cancelled.
 * @param onConfirm Callback to invoke if the deletion is confirmed.
 * @param title     Title to display.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DeleteDialog(
    message: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    title: String = stringResource(R.string.delete_dialog_title)
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    BottomSheetDialog(
        sheetState = sheetState,
        title = title,
        onDismiss = onCancel,
        icon = painterResource(R.drawable.ic_delete)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
            )
            FlowRow(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = dimensionResource(R.dimen.padding_vertical))
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onCancel()
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.button_cancel))
                }
                TextButton(
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal_between)),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            onConfirm()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors().copy(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(R.string.button_delete))
                }
            }
        }
    }
}
