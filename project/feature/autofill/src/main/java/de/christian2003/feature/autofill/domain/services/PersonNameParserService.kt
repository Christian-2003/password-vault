package de.christian2003.feature.autofill.domain.services

import de.christian2003.feature.autofill.domain.entities.AutofillType


/**
 * Service for parsing a full names into individual parts.
 */
internal interface PersonNameParserService {

    /**
     * Parses the specified full name into individual parts.
     *
     * @param fullName  Full name to parse.
     * @return          Individual parts of the name parsed into individual parts that are
     *                  mapped to their corresponding autofill type.
     */
    suspend fun parseNameToParts(fullName: String): Map<AutofillType, String>

}
