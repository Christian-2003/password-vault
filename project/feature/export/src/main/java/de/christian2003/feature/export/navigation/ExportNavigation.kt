package de.christian2003.feature.export.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import de.christian2003.feature.export.presentation.ui.export.ExportScreen
import de.christian2003.feature.export.presentation.viewmodels.ExportViewModel
import kotlinx.serialization.Serializable


// =============== Public destinations ===============

@Serializable
object ExportDestination


/**
 * Navigation destination for the screen through which to export data.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
fun NavGraphBuilder.exportDestination(
    onNavigateUp: () -> Unit
) {
    composable<ExportDestination> {
        val viewModel: ExportViewModel = hiltViewModel()

        ExportScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp
        )
    }
}
