package de.christian2003.feature.analysis.domain.repositories

import de.christian2003.feature.analysis.domain.entities.SecurityResultOverview


/**
 * Repository to store security result overviews.
 */
internal interface SecurityResultRepository {

    /**
     * Adds the specified security result overview to the repository.
     *
     * @param overview  Overview to add.
     */
    fun addSecurityResultOverview(overview: SecurityResultOverview)

    /**
     * Gets the latest security result overview. May be null if no overview is available.
     *
     * @return  Latest security result overview or null if unavailable.
     */
    fun getLatestSecurityResultOverview(): SecurityResultOverview?

    /**
     * Clears the stored security result overviews.
     */
    fun clear()

}
