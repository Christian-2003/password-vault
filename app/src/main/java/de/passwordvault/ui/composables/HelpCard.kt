package de.passwordvault.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.passwordvault.R


/**
 * Displays a help card that can be dismissed.
 *
 * @param text      Text to display within the help card.
 * @param onDismiss Callback invoked to dismiss the help.
 */
@Composable
fun HelpCard(
    text: String,
    onDismiss: () -> Unit
) {
    Card(
        text = text,
        foregroundColor = MaterialTheme.colorScheme.onTertiaryContainer,
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
        icon = painterResource(R.drawable.ic_help)
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(stringResource(R.string.button_dismiss_help))
            }
        }
    }
}
