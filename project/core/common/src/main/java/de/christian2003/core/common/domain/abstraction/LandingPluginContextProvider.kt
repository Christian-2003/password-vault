package de.christian2003.core.common.domain.abstraction

import androidx.compose.runtime.Composable


/**
 * Provider for the plugin context.
 */
interface LandingPluginContextProvider {

    /**
     * Provides the plugin context.
     *
     * @return  Plugin context.
     */
    fun provideLandingPluginContext(): LandingPluginContext

}
