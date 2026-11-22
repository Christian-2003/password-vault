package de.christian2003.passwordvault.application

import de.christian2003.passwordvault.domain.entities.LegalPage
import de.christian2003.passwordvault.domain.repositories.LegalPagesRepository
import javax.inject.Inject


/**
 * Use case to get legal pages.
 *
 * @param legalPagesRepository  Repository to access legal pages.
 */
internal class GetLegalPageUseCase @Inject constructor(
    private val legalPagesRepository: LegalPagesRepository
) {

    /**
     * Retrieves the "Privacy Policy" legal page. May be null if page cannot be loaded.
     *
     * @return  Legal page.
     */
    suspend fun getPrivacyPolicy(): LegalPage? {
        return legalPagesRepository.getPrivacyPolicyPage()
    }


    /**
     * Retrieves the "Terms of Service" legal page. May be null if page cannot be loaded.
     *
     * @return  Legal page.
     */
    suspend fun getTermsOfService(): LegalPage? {
        return legalPagesRepository.getTermsOfServicePage()
    }

}
