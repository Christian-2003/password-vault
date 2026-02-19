package de.christian2003.feature.autofill.domain.services

import de.christian2003.feature.autofill.domain.entities.AutofillType


/**
 * Service for parsing a full date into individual parts.
 */
internal interface DateParserService {

    /**
     * Parses the specified full date into individual parts.
     *
     * @param fullDate  Full date to parse.
     * @return          Individual parts of the date parsed into individual parts that are mapped to
     *                  their corresponding autofill type.
     */
    suspend fun parseDateToParts(fullDate: String): Map<AutofillType, String>

}
