package de.christian2003.feature.export.presentation.ui.export

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.christian2003.feature.export.domain.entities.ExportProgress
import de.christian2003.feature.export.R


/**
 * View displays the progress updates to the user while their export runs.
 *
 * @param progress      Current progress.
 * @param onNavigateUp  Callback to navigate up the nav stack.
 * @param modifier      Modifier.
 */
@Composable
internal fun ProgressView(
    progress: ExportProgress,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var buttonsVisible: Boolean by rememberSaveable { mutableStateOf(true) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(
                horizontal = dimensionResource(de.christian2003.core.ui.R.dimen.margin_horizontal)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (progress.progress == 0f) {
                CircularWavyProgressIndicator()
            }
            else {
                CircularProgressIndicator(
                    progress = { progress.progress }
                )
            }
            Text(
                text = stringResource(R.string.export_progress_labelProcessing),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )
            Text(
                text = stringResource(R.string.export_progress_labelLeave),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
            )
            AnimatedVisibility(buttonsVisible) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onNavigateUp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                    ) {
                        Text(stringResource(R.string.export_progress_buttonContinue))
                    }
                    OutlinedButton(
                        onClick = {
                            buttonsVisible = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(de.christian2003.core.ui.R.dimen.padding_vertical))
                    ) {
                        Text(stringResource(R.string.export_progress_buttonStay))
                    }
                }
            }
        }
    }
}
