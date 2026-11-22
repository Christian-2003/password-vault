package de.christian2003.passwordvault.application

import de.christian2003.passwordvault.domain.repositories.LegalPagesRepository
import javax.inject.Inject


/**
 * Use case to accept a new version of a legal page.
 *
 * @param legalPagesRepository  Repository to access legal pages.
 */
internal class AcceptLegalPageUseCase @Inject constructor(
    private val legalPagesRepository: LegalPagesRepository
) {

    /**
     * Accepts the new version of the privacy policy.
     */
    suspend fun acceptPrivacyPolicy() {
        legalPagesRepository.acceptPrivacyPolicy()
    }


    /**
     * Accepts the new version of the terms of service.
     */
    suspend fun acceptTermsOfService() {
        legalPagesRepository.acceptTermsOfService()
    }

}
