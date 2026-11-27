package de.christian2003.feature.analysis.presentation.ui.summary

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import de.christian2003.core.common.domain.abstraction.LandingPluginContext
import de.christian2003.feature.analysis.navigation.AnalysisDestination
import de.christian2003.feature.analysis.presentation.viewmodels.SummaryPluginViewModel


@Composable
internal fun SummaryPluginView(
    context: LandingPluginContext,
    viewModel: SummaryPluginViewModel = hiltViewModel()
) {
    Column() {
        Text("Analysis summary")
        Button(
            onClick = {
                context.navigate(AnalysisDestination)
            }
        ) {
            Text("Run Analysis")
        }
    }

}
