package de.christian2003.feature.autofill.infrastructure.services

import de.christian2003.feature.autofill.domain.entities.AutofillType
import de.christian2003.feature.autofill.domain.services.PersonNameParserService
import javax.inject.Inject


/**
 * Implementation for the service to parse a full names into individual parts. The service uses simple
 * heuristics to parse western-style names (mainly european and north american styles) into parts.
 */
internal class HeuristicPersonNameParserService @Inject constructor(): PersonNameParserService {

    /**
     * Name prefixes.
     */
    private val prefixes = setOf(
        "mr", "mrs", "ms", "miss", "dr", "prof", "sir", "madam", //English
        "herr", "frau", "dipl", "ing", "mag", "dipl-ing", //German
        "m", "mme", "mlle", "monsieur", "madame", "mademoiselle", "dr", "pr", //French
        "sr", "sra", "srta", "don", "doña", "dr", "dra", "lic" //Spanish
    )

    /**
     * Name suffixes.
     */
    private val suffixes = setOf(
        "jr", "sr", "ii", "iii", "iv", "phd", "md", "dds", "esq", //English
        "mba", "msc", "bsc", //German
        "phd", "hdr", //French
    )

    /**
     * Surname particles.
     */
    private val surnameParticles = setOf(
        "von", "vom", "zu", "zur", "zum", "von und zu", //German
        "de", "du", "des", "d'", //French
        "del", "de la", "de los", "de las", //Spanish
        "van", "van der", "van den", //Dutch (common in Germany too)
        "di", "da", "della", //Italian (often appears in DE/FR regions)
        "la", "le" //General Romance
    )


    /**
     * Parses the specified full name into individual parts.
     *
     * @param fullName  Full name to parse.
     * @return          Individual parts of the name parsed into individual parts that are
     *                  mapped to their corresponding autofill type.
     */
    override suspend fun parseNameToParts(fullName: String): Map<AutofillType, String> {
        if (fullName.isBlank()) {
            return emptyMap()
        }

        val normalized: String = fullName.replace(Regex("\\s+"), " ").trim()

        val result: MutableMap<AutofillType, String> = mutableMapOf()
        result[AutofillType.PersonFullName] = normalized

        val tokens: MutableList<String> = normalized.split(" ").toMutableList()

        if (tokens.isEmpty()) {
            return result
        }

        //Detect prefix:
        val firstTokenLower = tokens.first().lowercase().removeSuffix(".")
        if (prefixes.contains(firstTokenLower)) {
            result[AutofillType.PersonNamePrefix] = tokens.removeAt(0)
        }

        if (tokens.isEmpty()) {
            return result
        }

        //Detect suffix:
        val lastTokenLower = tokens.last().lowercase().removeSuffix(".")
        if (suffixes.contains(lastTokenLower)) {
            result[AutofillType.PersonNameSuffix] = tokens.removeAt(tokens.lastIndex)
        }

        if (tokens.isEmpty()) {
            return result
        }

        //Single token (mononym):
        if (tokens.size == 1) {
            result[AutofillType.PersonFirstName] = tokens[0]
            return result
        }

        //Detect last name with particles:
        val (lastName, lastNameStartIndex) = extractLastName(tokens)

        result[AutofillType.PersonLastName] = lastName
        result[AutofillType.PersonFirstName] = tokens.first()

        val middleTokens = if (lastNameStartIndex > 1) {
            tokens.subList(1, lastNameStartIndex)
        } else {
            emptyList()
        }

        //Middle name / initial:
        if (middleTokens.isNotEmpty()) {
            if (middleTokens.size == 1 && middleTokens[0].matches(Regex("^[A-Z]\\.?$"))) {
                result[AutofillType.PersonMiddleInitial] = middleTokens[0]
            } else {
                val middleName: String = middleTokens.joinToString(" ")
                result[AutofillType.PersonMiddleName] = middleName
                result[AutofillType.PersonMiddleInitial] = middleName.first { it.isLetterOrDigit() }.toString()
            }
        }

        return result
    }


    /**
     * Extracts the last name from the specified token-list.
     *
     * @param tokens    List of tokens.
     * @return          Last name.
     */
    private fun extractLastName(tokens: List<String>): Pair<String, Int> {
        if (tokens.isEmpty()) {
            return Pair("", -1)
        }

        val lowerTokens: List<String> = tokens.map { it.lowercase() }

        val startIndex: Int = tokens.lastIndex
        var particleStart: Int = startIndex //Always include final token as surname base

        // Walk backwards to include particles BEFORE surname
        while (particleStart > 0) {
            val oneWord: String = lowerTokens[particleStart - 1]

            val twoWord: String? = if (particleStart > 1) {
                "${lowerTokens[particleStart - 2]} ${lowerTokens[particleStart - 1]}"
            } else {
                null
            }

            val threeWord: String? = if (particleStart > 2) {
                "${lowerTokens[particleStart - 3]} ${lowerTokens[particleStart - 2]} ${lowerTokens[particleStart - 1]}"
            } else {
                null
            }

            when {
                threeWord != null && surnameParticles.contains(threeWord) -> particleStart -= 3
                twoWord != null && surnameParticles.contains(twoWord) -> particleStart -= 2
                surnameParticles.contains(oneWord) -> particleStart -= 1
                else -> break
            }
        }

        val lastName = tokens.subList(particleStart, tokens.size).joinToString(" ")

        return lastName to particleStart
    }

}
