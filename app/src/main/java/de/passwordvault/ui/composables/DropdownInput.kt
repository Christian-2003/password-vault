package de.passwordvault.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import de.passwordvault.R


/**
 * Composable displays a dropdown input through which the user can select a string from a number
 * of dropdown options.
 *
 * @param selected          String that is selected. Pass null to not select any option.
 * @param onSelectedChange  Callback invoked once the selection changes.
 * @param label             Label for the dropdown.
 * @param options           List of options from which the user can choose.
 * @param modifier          Modifier.
 * @param prefixIcon        Optional icon displayed in front of the dropdown.
 * @param errorMessage      Optional error message to display.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInput(
    selected: String?,
    onSelectedChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    prefixIcon: Painter? = null,
    errorMessage: String? = null
) {
    var expanded: Boolean by remember { mutableStateOf(false) }

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
        modifier = modifier
    ) {
        if (prefixIcon != null) {
            Icon(
                painter = prefixIcon,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "",
                modifier = Modifier
                    .padding(
                        top = dimensionResource(R.dimen.padding_material_text_field_top),
                        end = dimensionResource(R.dimen.padding_horizontal_between)
                    )
                    .size(dimensionResource(R.dimen.image_xs))
            )
        }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = it
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selected ?: "",
                onValueChange = { /* Do nothing since value cannot be changed manually */ },
                label = {
                    Text(
                        text = label
                    )
                },
                isError = errorMessage != null,
                supportingText = supportingTextView,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(
                                    vertical = dimensionResource(R.dimen.padding_vertical_between)
                                )
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelectedChange(option)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }

}
