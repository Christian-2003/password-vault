package de.christian2003.passwordvault.domain.repositories

import de.christian2003.passwordvault.domain.entities.LegalPage


/**
 * Repository through which to access information about the legal pages (i.e. "Privacy Policy" and
 * "Terms of Service") of Password Vault.
 */
internal interface LegalPagesRepository {

    /**
     * Returns the legal page of the privacy policy.
     * If privacy policy cannot be loaded, null may be returned.
     *
     * @return  Privacy policy legal page or null.
     */
    suspend fun getPrivacyPolicyPage(): LegalPage?

    /**
     * Checks whether a new version of the privacy policy is available.
     *
     * @return  Whether a new version is available.
     */
    suspend fun isNewPrivacyPolicyAvailable(): Boolean

    /**
     * Accepts the current version of the privacy policy.
     */
    suspend fun acceptPrivacyPolicy()


    /**
     * Returns the legal page of the terms of service.
     * If terms of service cannot be loaded, null may be returned.
     *
     * @return  Terms of service legal page or null.
     */
    suspend fun getTermsOfServicePage(): LegalPage?

    /**
     * Checks whether a new version of the terms of service is available.
     *
     * @return  Whether a new version is available.
     */
    suspend fun isNewTermsOfServiceAvailable(): Boolean

    /**
     * Accepts the current version of the terms of service.
     */
    suspend fun acceptTermsOfService()

}
