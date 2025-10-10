package de.christian2003.passwordvault.plugin.presentation.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.christian2003.passwordvault.R
import kotlinx.coroutines.android.awaitFrame


/**
 * Displays a dialog through which a single value can be edited.
 *
 * @param value                 Value to edit.
 * @param canValueBeBlank       Whether the edited value can be blank.
 * @param label                 Label for the text field.
 * @param title                 Title for the dialog.
 * @param onDismiss             Callback invoked to dismiss the dialog without saving the edited value.
 * @param onSave                Callback invoked to dismiss the dialog and save the edited value.
 * @param primaryButtonText     Text for the primary button (e.g. "OK").
 * @param secondaryButtonText   Text for the secondary button (e.g. "Cancel").
 */
@Composable
fun EditValueDialog(
    value: String,
    canValueBeBlank: Boolean,
    label: String,
    title: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    primaryButtonText: String = stringResource(R.string.button_ok),
    secondaryButtonText: String = stringResource(R.string.button_cancel)
) {
    val focusRequester: FocusRequester = remember { FocusRequester() }
    var mutableValue: String by rememberSaveable { mutableStateOf(value) }

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }

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
                TextInput(
                    value = mutableValue,
                    onValueChange = {
                        mutableValue = it
                    },
                    label = label,
                    focusRequester = focusRequester,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 24.dp
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onSave(mutableValue)
                        }
                    )
                )
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    TextButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(secondaryButtonText)
                    }
                    TextButton(
                        enabled = canValueBeBlank || mutableValue.isNotBlank(),
                        onClick = {
                            onSave(mutableValue)
                        },
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
                    ) {
                        Text(primaryButtonText)
                    }
                }
            }
        }
    }
}
