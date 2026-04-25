package de.christian2003.feature.analysis.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import de.christian2003.feature.analysis.presentation.ui.analysis.AnalysisScreen
import de.christian2003.feature.analysis.presentation.viewmodels.AnalysisViewModel
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

// =============== Public destinations ===============

@Serializable
object AnalysisDestination


/**
 * Destination for the password security analysis.
 *
 * @param onNavigateUp          Callback invoked to navigate up the navigation stack.
 * @param onNavigateToAccount   Callback invoked to navigate to the specified account.
 */
fun NavGraphBuilder.analysisDestination(
    onNavigateUp: () -> Unit,
    onNavigateToAccount: (Uuid) -> Unit
) {
    composable<AnalysisDestination> {
        val viewModel: AnalysisViewModel = hiltViewModel()

        AnalysisScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onNavigateToAccount = onNavigateToAccount
        )
    }
}
