package de.christian2003.passwordvault.application

import de.christian2003.core.common.domain.abstraction.LandingPlugin
import javax.inject.Inject


/**
 * Use case to get a list of plugins for the landing view.
 *
 * @param plugins   Set of plugins.
 */
internal class GetLandingPluginsUseCase @Inject constructor (
    private val plugins: Set<@JvmSuppressWildcards LandingPlugin>
) {

    /**
     * Returns a list of available plugins for the landing view.
     * The returned list is ordered by priority.
     *
     * @return  List of plugins.
     */
    fun getPlugins(): List<LandingPlugin> {
        return plugins.sortedBy { it.priority }
    }

}
