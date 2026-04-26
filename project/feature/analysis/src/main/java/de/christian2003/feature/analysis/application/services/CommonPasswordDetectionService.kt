package de.christian2003.feature.analysis.application.services

import de.christian2003.feature.analysis.domain.entities.LookupType
import de.christian2003.feature.analysis.domain.repositories.LookupRepository
import javax.inject.Inject


/**
 * Service detects common passwords.
 *
 * @param lookupRepository  Repository used to lookup common passwords.
 */
internal class CommonPasswordDetectionService @Inject constructor(
    private val lookupRepository: LookupRepository
) {

    /**
     * Set of common passwords.
     */
    var commonPasswords: Set<String>? = null


    /**
     * Prepares the service for the detection of common passwords.
     */
    suspend fun preparePasswordDetection() {
        commonPasswords = lookupRepository.getWords(LookupType.CommonPasswords)
    }


    /**
     * Cleanup and disposal after service was used.
     */
    fun cleanupPasswordDetection() {
        commonPasswords = null //Large set of words can now be cleared by GC
    }


    /**
     * Checks whether the specified password is a common password.
     *
     * @param password  Password to check.
     * @return          Whether the specified password is a common password.
     */
    fun isCommonPassword(password: String): Boolean {
        val lowercasePassword: String = password.lowercase()

        if (commonPasswords != null) {
            return lowercasePassword in commonPasswords!!
        }
        return false
    }

}
