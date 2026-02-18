package de.christian2003.feature.autofill.infrastructure.services

import de.christian2003.feature.autofill.domain.AddressParserService
import de.christian2003.feature.autofill.domain.entities.AutofillType
import javax.inject.Inject


/**
 * Implementation for the service to parse an address into individual parts. The service uses simple
 * heuristics to parse western-style addresses (mainly european and north american styles) into
 * parts. This service is intended to be used if the Android Geocoder (through
 * GeocoderAddressParserService) is not available.
 */
internal class HeuristicAddressParserService @Inject constructor() : AddressParserService {

    /**
     * Parses the specified full address into individual parts.
     *
     * @param fullAddress   Full address to parse.
     * @return              Individual parts of the address parsed into individual parts that are
     *                      mapped to their corresponding autofill type.
     */
    override suspend fun parseAddressToParts(fullAddress: String): Map<AutofillType, String> {
        if (fullAddress.isBlank()) {
            return emptyMap()
        }

        val normalized: String = fullAddress
            .replace("\n", ",")
            .replace(Regex("\\s+"), " ")
            .trim()

        val segments: MutableList<String> = normalized
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toMutableList()

        val result: MutableMap<AutofillType, String> = mutableMapOf()
        result[AutofillType.PostalAddress] = normalized

        //Detect country
        if (segments.isNotEmpty()) {
            val lastIndex: Int = segments.lastIndex
            val last: String = segments[lastIndex]

            if (last.matches(Regex("^[A-Za-zÀ-ÿ ]{3,}$"))) {
                result[AutofillType.AddressCountry] = last
                segments.removeAt(lastIndex)
            }
        }

        //Detect postal code and city
        val postalCityRegex = Regex("""\b(\d{4,5})\s+(.+)$""")

        val postalIndex: Int = segments.indexOfFirst { segment ->
            postalCityRegex.containsMatchIn(segment)
        }

        if (postalIndex != -1) {
            val segment: String = segments[postalIndex]
            val match: MatchResult? = postalCityRegex.find(segment)

            if (match != null) {
                result[AutofillType.PostalCode] = match.groupValues[1]
                result[AutofillType.AddressLocality] = match.groupValues[2]
                segments.removeAt(postalIndex)
            }
        }

        // Detect street and house number
        val streetRegex = Regex("""^(.+?)\s+(\d+[A-Za-z]?)$""") // street name first
        val reverseStreetRegex = Regex("""^(\d+[A-Za-z]?)\s+(.+)$""") // number first

        segments.firstOrNull()?.let { streetSegment ->
            val match: MatchResult? = streetRegex.find(streetSegment)
            val reverseMatch: MatchResult? = reverseStreetRegex.find(streetSegment)

            when {
                match != null -> result[AutofillType.AddressStreet] = streetSegment
                reverseMatch != null -> result[AutofillType.AddressStreet] = streetSegment
                else -> result[AutofillType.AddressStreet] = streetSegment //Assume it's a street as fallback
            }
        }

        return result
    }

}
