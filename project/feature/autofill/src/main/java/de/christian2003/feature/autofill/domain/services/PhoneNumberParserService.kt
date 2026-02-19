package de.christian2003.feature.autofill.domain.services

import de.christian2003.feature.autofill.domain.entities.AutofillType


/**
 * Service for parsing a full phone number into individual parts.
 */
internal interface PhoneNumberParserService {

    /**
     * Parses the specified full phone number into individual parts.
     *
     * @param fullNumber    Full phone number to parse.
     * @return              Individual parts of the phone number parsed into individual parts that
     *                      are mapped to their corresponding autofill type.
     */
    suspend fun parsePhoneNumberToParts(fullNumber: String): Map<AutofillType, String>

}
