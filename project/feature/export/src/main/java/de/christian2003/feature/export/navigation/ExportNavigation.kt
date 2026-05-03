package de.christian2003.feature.export.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.feature.export.presentation.ui.export.ExportScreen
import de.christian2003.feature.export.presentation.ui.exports.ExportsScreen
import de.christian2003.feature.export.presentation.viewmodels.ExportViewModel
import de.christian2003.feature.export.presentation.viewmodels.ExportsViewModel
import kotlinx.serialization.Serializable


// =============== Public destinations ===============

@Serializable
object ExportsFlow


fun NavGraphBuilder.exportsFlow(
    navController: NavController
) {
    navigation<ExportsFlow>(
        startDestination = ExportsDestination
    ) {

        exportsDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onNavigateToExport = { exportServiceId ->
                navController.navigate(ExportDestination(exportServiceId))
            }
        )

        exportDestination(
            onNavigateUp = {
                navController.navigateUp()
            }
        )

    }
}


// =============== Internal destinations ===============

@Serializable
private object ExportsDestination

@Serializable
internal data class ExportDestination(
    val exportServiceId: String
)


private fun NavGraphBuilder.exportsDestination(
    onNavigateUp: () -> Unit,
    onNavigateToExport: (String) -> Unit
) {
    composable<ExportsDestination> {
        val viewModel: ExportsViewModel = hiltViewModel()

        ExportsScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
                onNavigateToExport = onNavigateToExport
        )
    }
}


/**
 * Navigation destination for the screen through which to export data.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
private fun NavGraphBuilder.exportDestination(
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
