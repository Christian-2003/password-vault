package de.christian2003.feature.search.navigation

import android.widget.SearchView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.feature.search.presentation.ui.search.SearchScreen
import de.christian2003.feature.search.presentation.viewmodels.SearchViewModel
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


// =============== Public destinations ===============

@Serializable
object SearchFlow


/**
 * Flow for the search feature.
 *
 * @param navController         Navigation controller.
 * @param onNavigateToAccount   Callback invoked to navigate to the specified account.
 */
fun NavGraphBuilder.searchFlow(
    navController: NavController,
    onNavigateToAccount: (Uuid) -> Unit
) {
    navigation<SearchFlow>(
        startDestination = SearchDestination
    ) {
        searchDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onNavigateToAccount = onNavigateToAccount
        )
    }
}



// =============== Internal destinations ===============

@Serializable
private object SearchDestination


/**
 * Navigation destination for the search screen.
 *
 * @param onNavigateUp          Callback invoked to navigate up the navigation stack.
 * @param onNavigateToAccount   Callback invoked to navigate to the specified account.
 */
private fun NavGraphBuilder.searchDestination(
    onNavigateUp: () -> Unit,
    onNavigateToAccount: (Uuid) -> Unit
) {
    composable<SearchDestination> {
        val viewModel: SearchViewModel = hiltViewModel()

        SearchScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp,
            onNavigateToAccount = onNavigateToAccount
        )
    }
}
