package de.christian2003.core.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.R


/**
 * Composable displays an outlined text field whose value can be selected through a dropdown menu.
 *
 * @param items                     Items for the dropdown menu.
 * @param itemIcons                 List of icons for the items. Indices correspond to the items-list.
 * @param selectedItemIndex         Index of the selected item.
 * @param onSelectedItemIndexChange Callback invoked once the selection changes.
 * @param label                     Label.
 * @param modifier                  Modifier.
 * @param prefixIcon                Optional prefix icon.
 */
@Composable
fun DropdownInput(
    items: List<String>,
    itemIcons: List<Painter?>,
    selectedItemIndex: Int,
    onSelectedItemIndexChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    prefixIcon: Painter? = null
) {
    var expanded: Boolean by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth()
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
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = it
            }
        ) {
            OutlinedTextField(
                value = items[selectedItemIndex],
                onValueChange = { },
                label = {
                    Text(label)
                },
                readOnly = true,
                trailingIcon = {
                    val animatedArrowRotation by animateFloatAsState(
                        targetValue = if (expanded) { 180F } else { 0F },
                        animationSpec = spring()
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_expand),
                        contentDescription = "",
                        modifier = Modifier.rotate(animatedArrowRotation)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                items.forEachIndexed { index, text ->
                    val iconPainter: Painter? = itemIcons.getOrNull(index)
                    DropdownMenuItem(
                        text = {
                            Text(text)
                        },
                        onClick = {
                            expanded = false
                            onSelectedItemIndexChange(index)
                        },
                        leadingIcon = {
                            if (iconPainter != null) {
                                Icon(
                                    painter = iconPainter,
                                    contentDescription = ""
                                )
                            }
                        },
                        contentPadding = PaddingValues(
                            horizontal = dimensionResource(R.dimen.padding_horizontal),
                            vertical = dimensionResource(R.dimen.padding_vertical)
                        )
                    )
                }
            }
        }
    }
}
