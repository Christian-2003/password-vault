package de.christian2003.feature.search.application.usecases

import de.christian2003.feature.search.domain.repositories.SearchConfigRepository
import javax.inject.Inject


/**
 * Use case to add a query to the list of recent queries.
 *
 * @param configRepository  Repository for the search config.
 */
internal class AddRecentQueryUseCase @Inject constructor(
    private val configRepository: SearchConfigRepository
) {

    /**
     * Adds the specified query to the recent queries.
     *
     * @param query Query to add.
     */
    fun addRecentQuery(query: String) {
        configRepository.addRecentQuery(query)
    }

}
