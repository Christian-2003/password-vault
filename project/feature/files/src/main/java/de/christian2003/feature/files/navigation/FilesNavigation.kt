package de.christian2003.feature.files.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.feature.files.ui.directory.DirectoryScreen
import de.christian2003.feature.files.viewmodels.DirectoryViewModel
import kotlinx.serialization.Serializable


// =============== Public destinations ===============

@Serializable
object DirectoriesFlow


fun NavGraphBuilder.directoriesFlow(
    navController: NavController
) {
    navigation<DirectoriesFlow>(
        startDestination = DirectoryDestination("")
    ) {

        directoryDestination(
            onNavigateUp = {
                navController.navigateUp()
            }
        )

    }
}


// =============== Private destinations ===============

@Serializable
internal data class DirectoryDestination(
    val internalDirectoryPath: String
)


/**
 * Navigation destination to view the contents of a directory.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
private fun NavGraphBuilder.directoryDestination(
    onNavigateUp: () -> Unit
) {
    composable<DirectoryDestination> {
        val viewModel: DirectoryViewModel = hiltViewModel()

        DirectoryScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp
        )
    }
}
