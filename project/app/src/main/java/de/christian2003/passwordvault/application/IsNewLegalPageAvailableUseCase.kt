package de.christian2003.passwordvault.application

import de.christian2003.passwordvault.domain.repositories.LegalPagesRepository
import javax.inject.Inject


/**
 * Use case to check whether new legal documents are available.
 *
 * @param legalPagesRepository  Repository to access legal pages.
 */
internal class IsNewLegalPageAvailableUseCase @Inject constructor(
    private val legalPagesRepository: LegalPagesRepository
) {

    /**
     * Checks whether a new privacy policy is available.
     *
     * @return  Whether a new privacy policy is available.
     */
    suspend fun isNewPrivacyPolicyAvailable(): Boolean {
        return legalPagesRepository.isNewPrivacyPolicyAvailable()
    }


    /**
     * Checks whether a new terms of service is available.
     *
     * @return  Whether a new terms of service is available.
     */
    suspend fun isNewTermsOfServiceAvailable(): Boolean {
        return legalPagesRepository.isNewTermsOfServiceAvailable()
    }

}
