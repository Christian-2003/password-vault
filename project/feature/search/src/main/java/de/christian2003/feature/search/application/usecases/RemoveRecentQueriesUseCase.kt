package de.christian2003.feature.search.application.usecases

import de.christian2003.feature.search.domain.repositories.SearchConfigRepository
import javax.inject.Inject


/**
 * Use case to remove all most recent queries.
 *
 * @param configRepository  Repository for the search config.
 */
internal class RemoveRecentQueriesUseCase @Inject constructor(
    private val configRepository: SearchConfigRepository
) {

    /**
     * Removes all recent queries.
     */
    fun removeRecentQueries() {
        configRepository.removeRecentQueries()
    }

}
