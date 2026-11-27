package de.christian2003.passwordvault.presentation.ui.home

import androidx.navigation.NavController
import de.christian2003.core.common.domain.abstraction.LandingPluginContext
import de.christian2003.core.common.domain.abstraction.LandingPluginContextProvider


/**
 * Provider for the plugin context using a NavController for navigation.
 *
 * @param navController Nav controller.
 */
internal class HomePluginContextProvider(
    private val navController: NavController
): LandingPluginContextProvider {

    /**
     * Provides the plugin context.
     *
     * @return  Plugin context.
     */
    override fun provideLandingPluginContext(): LandingPluginContext {
        return HomePluginContext(navController)
    }

}
