package de.christian2003.core.common.domain.abstraction

import androidx.compose.runtime.Composable


/**
 * Plugin for the landing page.
 */
interface LandingPlugin {

    /**
     * ID for the plugin.
     */
    val id: String

    /**
     * Priority for the plugin determines its position on the landing page.
     */
    val priority: Int

    /**
     * Composable content of the plugin.
     *
     * @param context   Context for the plugin.
     */
    @Composable
    fun Content(context: LandingPluginContext)

}
