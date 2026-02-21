package de.christian2003.feature.autofill.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import de.christian2003.feature.autofill.presentation.ui.settings.AutofillSettingsScreen
import de.christian2003.feature.autofill.presentation.viewmodels.AutofillSettingsViewModel
import kotlinx.serialization.Serializable


// =============== Public destinations ===============

@Serializable
object AutofillSettingsFlow


/**
 * Flow for the autofill settings.
 *
 * @param navController Navigation controller.
 */
fun NavGraphBuilder.autofillSettingsFlow(
    navController: NavController
) {
    navigation<AutofillSettingsFlow>(
        startDestination = AutofillSettingsDestination
    ) {
        autofillSettingsDestination(
            onNavigateUp = {
                navController.navigateUp()
            }
        )
    }
}



// =============== Internal destinations ===============

@Serializable
private object AutofillSettingsDestination


/**
 * Navigation destination for the autofill settings screen.
 *
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
private fun NavGraphBuilder.autofillSettingsDestination(
    onNavigateUp: () -> Unit
) {
    composable<AutofillSettingsDestination> {
        val viewModel: AutofillSettingsViewModel = hiltViewModel()

        AutofillSettingsScreen(
            viewModel = viewModel,
            onNavigateUp = onNavigateUp
        )
    }
}
