package de.christian2003.feature.analysis.application.services

import de.christian2003.feature.analysis.domain.entities.LookupType
import de.christian2003.feature.analysis.domain.repositories.LookupRepository
import javax.inject.Inject


/**
 * Service used to detect whether dictionary words are part of a password.
 *
 * @param lookupRepository  Repository used to lookup common dictionary words.
 */
internal class DictionaryDetectionService @Inject constructor(
    private val lookupRepository: LookupRepository
) {

    /**
     * Set contains the most common dictionary words.
     */
    var dictionaryWords: Set<String>? = null


    /**
     * Prepares the service for the detection of dictionary words.
     */
    suspend fun prepareDictionaryDetection() {
        dictionaryWords = lookupRepository.getWords(LookupType.DictionaryWords)
    }


    /**
     * Cleanup and disposal after the service was used.
     */
    fun cleanupDictionaryDetection() {
        dictionaryWords = null //Large set of words can now be cleared by GC
    }


    /**
     * Checks whether the specified password contains dictionary words.
     *
     * @param password  Password to check.
     * @return          Whether the specified password contains dictionary words.
     */
    fun containsDictionaryWords(password: String): Boolean {
        val lowercasePassword: String = password.lowercase()

        if (dictionaryWords != null) {
            dictionaryWords!!.forEach { word ->
                if (lowercasePassword.contains(word)) {
                    return true
                }
            }
        }
        return false
    }

}
