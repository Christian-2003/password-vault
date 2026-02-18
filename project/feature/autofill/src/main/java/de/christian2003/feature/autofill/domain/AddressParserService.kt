package de.christian2003.feature.autofill.domain

import de.christian2003.feature.autofill.domain.entities.AutofillType


/**
 * Service for parsing a full address into individual parts.
 */
internal interface AddressParserService {

    /**
     * Parses the specified full address into individual parts.
     *
     * @param fullAddress   Full address to parse.
     * @return              Individual parts of the address parsed into individual parts that are
     *                      mapped to their corresponding autofill type.
     */
    suspend fun parseAddressToParts(fullAddress: String): Map<AutofillType, String>

}
