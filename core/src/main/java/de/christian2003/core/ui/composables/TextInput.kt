package de.christian2003.core.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import de.christian2003.core.R
import kotlinx.coroutines.launch


/**
 * Composable displays an outlined text field through which the user can edit a value.
 *
 * @param value             Value to edit.
 * @param onValueChange     Callback invoked once the value changes.
 * @param label             Label for the text field.
 * @param modifier          Modifier.
 * @param prefixIcon        Optional Icon to display in front of the text input.
 * @param keyboardOptions   Optional keyboard options.
 * @param suffixLabel       Optional suffix label to display within the text input.
 * @param trailingIcon      Optional trailing icon to display within the text input. If the error
 *                          message passed is not null, this icon will be replaced with an error
 *                          icon.
 * @param errorMessage      Error message to display.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    prefixIcon: Painter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    suffixLabel: String? = null,
    trailingIcon: Painter? = null,
    errorMessage: String? = null
) {
    val scope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    //Text displayed as suffix:
    val suffixView: (@Composable () -> Unit)? = if (suffixLabel != null) {
        @Composable {
            Text(
                text = suffixLabel,
                color = if (errorMessage == null) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    } else {
        null
    }

    //Trailing icon displayed if an error occurs:
    val trailingIconView: (@Composable () -> Unit)? = if (errorMessage != null) {
        @Composable {
            Icon(
                painter = painterResource(R.drawable.ic_error),
                tint = MaterialTheme.colorScheme.error,
                contentDescription = errorMessage
            )
        }
    } else if (trailingIcon != null) {
        @Composable {
            Icon(
                painter = trailingIcon,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = ""
            )
        }
    } else {
        null
    }

    //Supporting text displayed if an error occurs:
    val supportingTextView: (@Composable () -> Unit)? = if (errorMessage != null) {
        @Composable {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    } else {
        null
    }

    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (prefixIcon != null) {
            Icon(
                painter = prefixIcon,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(
                        top = dimensionResource(R.dimen.padding_material_text_field_top),
                        end = dimensionResource(R.dimen.padding_horizontal)
                    )
                    .size(dimensionResource(R.dimen.image_small))
            )
        }
        OutlinedTextField(
            value = value,
            singleLine = true,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label
                )
            },
            keyboardOptions = keyboardOptions,
            suffix = suffixView,
            isError = errorMessage != null,
            trailingIcon = trailingIconView,
            supportingText = supportingTextView,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent {
                    if (it.isFocused) {
                        scope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
        )
    }
}
