package de.christian2003.core.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import de.christian2003.core.ui.R
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp


/**
 * Composable displays an outlined text field through which the user can edit a value.
 *
 * @param value                 Value to edit.
 * @param onValueChange         Callback invoked once the value changes.
 * @param label                 Label for the text field.
 * @param modifier              Modifier.
 * @param prefixIcon            Optional Icon to display in front of the text input.
 * @param keyboardOptions       Optional keyboard options.
 * @param keyboardActions       Optional keyboard actions.
 * @param suffixLabel           Optional suffix label to display within the text input.
 * @param trailingIcon          Optional trailing icon to display within the text input. If the error
 *                              message passed is not null, this icon will be replaced with an error
 *                              icon.
 * @param errorMessage          Error message to display.
 * @param enabled               Whether the text input is enabled or not.
 * @param isPassword            Whether the text input is used to display a password.
 * @param visualTransformation  Visual transformation for the text displayed.
 * @param focusRequester        Focus requester.
 * @param textStyle             Text style.
 */
@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    prefixIcon: Painter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    suffixLabel: String? = null,
    trailingIcon: Painter? = null,
    errorMessage: String? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    focusRequester: FocusRequester = FocusRequester(),
    textStyle: TextStyle = LocalTextStyle.current
) {
    var selection by remember { mutableStateOf(TextRange(value.length)) }
    TextInput(
        value = TextFieldValue(AnnotatedString(value), selection),
        onValueChange = { textFieldValue ->
            selection = textFieldValue.selection
            onValueChange(textFieldValue.text)
        },
        label = label,
        modifier = modifier,
        prefixIcon = prefixIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        suffixLabel = suffixLabel,
        trailingIcon = trailingIcon,
        errorMessage = errorMessage,
        enabled = enabled,
        isPassword = isPassword,
        visualTransformation = visualTransformation,
        focusRequester = focusRequester,
        textStyle = textStyle
    )
}


/**
 * Composable displays an outlined text field through which the user can edit a value.
 *
 * @param value                 Value to edit.
 * @param onValueChange         Callback invoked once the value changes.
 * @param label                 Label for the text field.
 * @param modifier              Modifier.
 * @param prefixIcon            Optional Icon to display in front of the text input.
 * @param keyboardOptions       Optional keyboard options.
 * @param keyboardActions       Optional keyboard actions.
 * @param suffixLabel           Optional suffix label to display within the text input.
 * @param trailingIcon          Optional trailing icon to display within the text input. If the error
 *                              message passed is not null, this icon will be replaced with an error
 *                              icon.
 * @param errorMessage          Error message to display.
 * @param enabled               Whether the text input is enabled or not.
 * @param isPassword            Whether the text input is used to display a password.
 * @param visualTransformation  Visual transformation for the text displayed.
 * @param focusRequester        Focus requester.
 * @param textStyle             Text style.
 */
@Composable
fun TextInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    prefixIcon: Painter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    suffixLabel: String? = null,
    trailingIcon: Painter? = null,
    errorMessage: String? = null,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    focusRequester: FocusRequester = FocusRequester(),
    textStyle: TextStyle = LocalTextStyle.current
) {
    val scope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var isPasswordVisible: Boolean by remember { mutableStateOf(false) }

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
    val trailingIconView: (@Composable () -> Unit)? = if (isPassword) {
        @Composable {
            IconButton(
                onClick = {
                    isPasswordVisible = !isPasswordVisible
                }
            ) {
                Icon(
                    painter = if (isPasswordVisible) {
                        painterResource(R.drawable.ic_visibility_off)
                    } else {
                        painterResource(R.drawable.ic_visibility_on)
                    },
                    contentDescription = "",
                    tint = if (errorMessage != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        IconButtonDefaults.iconButtonColors().contentColor
                    }
                )
            }
        }
    }
    else if (errorMessage != null) {
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
                        top = 8.dp,
                        end = dimensionResource(R.dimen.padding_horizontal)
                    )
                    .size(dimensionResource(R.dimen.image_xs))
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
            keyboardActions = keyboardActions,
            enabled = enabled,
            suffix = suffixView,
            isError = errorMessage != null,
            trailingIcon = trailingIconView,
            supportingText = supportingTextView,
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                visualTransformation
            },
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent {
                    if (it.isFocused) {
                        scope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                }
                .focusRequester(focusRequester)
        )
    }
}
