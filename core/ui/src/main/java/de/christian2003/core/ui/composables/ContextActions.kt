package de.christian2003.core.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.christian2003.core.ui.R


/**
 * Shows a button through which to display a list of context actions.
 *
 * @param actions   List of context actions.
 */
@Composable
fun ContextActions(
    actions: List<ContextActionBase>
) {
    var isDropdownVisible: Boolean by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            isDropdownVisible = !isDropdownVisible
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_more),
            contentDescription = ""
        )
        DropdownMenu(
            expanded = isDropdownVisible,
            onDismissRequest = {
                isDropdownVisible = false
            }
        ) {
            actions.forEach { action ->
                if (action.type == ContextActionType.Divider) {
                    //Divider:
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                else {
                    //Click action:
                    action as ContextAction
                    DropdownMenuItem(
                        text = {
                            Text(action.text)
                        },
                        leadingIcon = if (action.icon != null) {
                            {
                                Icon(
                                    painter = action.icon,
                                    contentDescription = ""
                                )
                            }
                        } else {
                            null
                        },
                        onClick = {
                            isDropdownVisible = false
                            action.onClick()
                        }
                    )
                }
            }
        }
    }
}


/**
 * Type for context actions.
 */
enum class ContextActionType() {
    Click,
    Divider
}


/**
 * Base class for context actions.
 *
 * @param type  Type of the context action.
 */
abstract class ContextActionBase(
    val type: ContextActionType
) { }


/**
 * Clickable context action.
 *
 * @param text      Text for the context action.
 * @param icon      Icon for the context action.
 * @param onClick   Callback invoked once the context action is invoked.
 */
class ContextAction(
    val text: String,
    val icon: Painter? = null,
    val onClick: () -> Unit
): ContextActionBase(
    type = ContextActionType.Click
) { }


/**
 * Divider that can be added between context actions.
 */
class ContextActionDivider(): ContextActionBase(
    type = ContextActionType.Divider
) { }
