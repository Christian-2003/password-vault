package de.christian2003.passwordvault.presentation.ui.home

import androidx.navigation.NavController
import de.christian2003.core.common.domain.abstraction.LandingPluginContext


/**
 * Context for the plugins displayed in the HomeView.
 *
 * @param navController Nav controller used to handle navigation.
 */
internal class HomePluginContext(
    private val navController: NavController
): LandingPluginContext {

    /**
     * Navigates to the specified route.
     *
     * @param route Route to which to navigate.
     */
    override fun navigate(route: Any) {
        navController.navigate(route)
    }

}
