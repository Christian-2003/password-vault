package de.christian2003.core.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity


/**
 * Button that can switch between a label and loading indicator.
 *
 * @param label     Label for the button.
 * @param isLoading Whether to display the loading indicator instead of the label.
 * @param onClick   Callback invoked once the button is clicked.
 * @param modifier  Modifier.
 * @param enabled   Whether the button is enabled.
 */
@Composable
fun LoadingIndicatorButton(
    label: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                modifier = Modifier.alpha(if (isLoading) { 0.0f } else { 1.0f })
            )
            if (isLoading) {
                CircularProgressIndicator(
                    color = ButtonDefaults.buttonColors().contentColor,
                    modifier = Modifier.size(with(LocalDensity.current) { MaterialTheme.typography.labelLarge.lineHeight.toDp() })
                )
            }
        }
    }
}
