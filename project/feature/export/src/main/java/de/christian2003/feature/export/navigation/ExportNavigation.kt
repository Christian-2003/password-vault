package de.christian2003.feature.export.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.feature.export.presentation.ui.export.ExportScreen
import de.christian2003.feature.export.presentation.ui.imprt.ImportScreen
import de.christian2003.feature.export.presentation.ui.services.ServicesScreen
import de.christian2003.feature.export.presentation.viewmodels.ExportViewModel
import de.christian2003.feature.export.presentation.viewmodels.ImportViewModel
import de.christian2003.feature.export.presentation.viewmodels.ServicesViewModel
import kotlinx.serialization.Serializable


// =============== Public destinations ===============

@Serializable
object ExportsFlow

@Serializable
object ImportsFlow


fun NavGraphBuilder.exportsFlow(
    navController: NavController
) {
    navigation<ExportsFlow>(
        startDestination = ServicesDestination(true)
    ) {

        servicesDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onServiceSelected = { serviceId ->
                navController.navigate(ExportDestination(serviceId))
            }
        )

        exportDestination(
            onNavigateUp = {
                navController.navigateUp()
            }
        )

    }
}


fun NavGraphBuilder.importsFlow(
    navController: NavController
) {
    navigation<ImportsFlow>(
        startDestination = ServicesDestination(false)
    ) {

        servicesDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onServiceSelected = { serviceId ->
                navController.navigate(ImportDestination(serviceId))
            }
        )

        importDestination(
            onNavigateUp = {
                navController.navigateUp()
            }
        )

    }
}


// =============== Internal destinations ===============

@Serializable
private data class ServicesDestination(
    val isExport: Boolean
)

@Serializable
internal data class ExportDestination(
    val exportServiceId: String
)

@Serializable
internal data class ImportDestination(
    val importServiceId: String
)


/**
 * Navigation destination for the screen through which customers can discover available export or
 * import services.
 *
 * @param onNavigateUp      Callback invoked to navigate up the navigation stack.
 * @param onServiceSelected Callback invoked once a service is selected.
 */
private fun NavGraphBuilder.servicesDestination(
    onNavigateUp: () -> Unit,
    onServiceSelected: (String) -> Unit
) {
    composable<ServicesDestination> {
        val viewModel: ServicesViewModel = hiltViewModel()

        ServicesScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onServiceSelected = onServiceSelected
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


/**
 * Navigation destination for the screen through which to import data.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
private fun NavGraphBuilder.importDestination(
    onNavigateUp: () -> Unit
) {
    composable<ImportDestination> {
        val viewModel: ImportViewModel = hiltViewModel()

        ImportScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp
        )
    }
}
