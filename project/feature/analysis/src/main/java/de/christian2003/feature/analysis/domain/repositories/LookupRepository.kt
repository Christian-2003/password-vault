package de.christian2003.feature.analysis.domain.repositories

import de.christian2003.feature.analysis.domain.entities.LookupType


/**
 * Repository that can be used to lookup words for the password analysis. Examples for looked-up
 * words are common passwords or dictionary words.
 */
internal interface LookupRepository {

    /**
     * Returns a set of words (can be up to 100k entries) from a lookup.
     *
     * @param type  Type of the lookup words to return.
     * @return      Set of words from the specified type.
     */
    suspend fun getWords(type: LookupType): Set<String>

}
