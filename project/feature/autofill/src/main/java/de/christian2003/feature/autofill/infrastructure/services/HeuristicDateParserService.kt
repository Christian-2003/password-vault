package de.christian2003.feature.autofill.infrastructure.services

import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.domain.services.DateParserService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject


/**
 * Implementation for the service to parse a full date into individual parts. The service uses simple
 * heuristics to parse western-style dates (mainly european and north american styles) into parts.
 */
internal class HeuristicDateParserService @Inject constructor(): DateParserService {

    /**
     * Common date formats that are recognized.
     */
    private val formatters = listOf(
        "yyyy-MM-dd",
        "dd.MM.yyyy",
        "d.M.yyyy",
        "dd/MM/yyyy",
        "d/M/yyyy",
        "MM/dd/yyyy",
        "M/d/yyyy",
        "dd-MM-yyyy",
        "d-M-yyyy",
        "yyyy/MM/dd",
        "yy-MM-dd",
        "dd.MM.yy",
        "d.M.yy",
        "dd/MM/yy",
        "d/M/yy",
        "MM/dd/yy",
        "M/d/yy",
        "dd-MM-yy",
        "d-M-yy",
        "yy/MM/dd"
    )


    /**
     * Parses the specified full date into individual parts.
     *
     * @param fullDate  Full date to parse.
     * @return          Individual parts of the date parsed into individual parts that are mapped to
     *                  their corresponding autofill type.
     */
    override suspend fun parseDateToParts(fullDate: String): Map<AutofillType, String> {
        if (fullDate.isBlank()) return emptyMap()

        val normalized = fullDate.trim()
        val result = mutableMapOf<AutofillType, String>()
        result[AutofillType.BirthDateFull] = normalized

        // Try parsing with each formatter
        for (pattern in formatters) {
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern)
                val date = LocalDate.parse(normalized, formatter)

                result[AutofillType.BirthDateDay] = date.dayOfMonth.toString()
                result[AutofillType.BirthDateMonth] = date.monthValue.toString()
                result[AutofillType.BirthDateYear] = date.year.toString()

                return result
            }
            catch (_: DateTimeParseException) {
                // Try next pattern
            }
        }

        // If none matched, just return the full string
        return result
    }

}
