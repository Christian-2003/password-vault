package de.christian2003.feature.analysis.application.usecases

import de.christian2003.feature.analysis.domain.entities.SecurityResultOverview
import de.christian2003.feature.analysis.domain.repositories.SecurityResultRepository
import javax.inject.Inject


/**
 * Use case to get the latest security result overview.
 *
 * @param securityResultRepository  Repository to access the overviews.
 */
internal class GetLatestSecurityResultOverviewUseCase @Inject constructor(
    private val securityResultRepository: SecurityResultRepository
) {

    /**
     * Returns the latest security result overview or null if no result is available.
     *
     * @return  Latest overview or null.
     */
    fun getLatestSecurityResultOverview(): SecurityResultOverview? {
        return securityResultRepository.getLatestSecurityResultOverview()
    }

}
