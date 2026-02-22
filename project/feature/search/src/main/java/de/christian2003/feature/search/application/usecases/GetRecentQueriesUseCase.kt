package de.christian2003.feature.search.application.usecases

import de.christian2003.feature.search.domain.repositories.SearchConfigRepository
import javax.inject.Inject


/**
 * Use case to get a list of the most recent queries.
 *
 * @param configRepository  Repository for the search config.
 */
internal class GetRecentQueriesUseCase @Inject constructor(
    private val configRepository: SearchConfigRepository
) {

    /**
     * Returns the most recent queries.
     *
     * @return  Most recent queries.
     */
    fun getQueries(): List<String> {
        return configRepository.getRecentQueries()
    }

}
