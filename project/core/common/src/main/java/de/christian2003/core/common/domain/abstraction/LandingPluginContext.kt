package de.christian2003.core.common.domain.abstraction


/**
 * Context for plugins on the landing page.
 */
interface LandingPluginContext {

    /**
     * Navigates to the specified route.
     *
     * @param route Route to which to navigate.
     */
    fun navigate(route: Any)

}
