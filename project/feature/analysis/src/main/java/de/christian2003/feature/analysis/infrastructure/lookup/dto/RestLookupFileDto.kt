package de.christian2003.feature.analysis.infrastructure.lookup.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * DTO contains information about lookup files (e.g. dictionaries or common passwords) from the
 * REST endpoint to query the lookup data.
 *
 * @param language      Language of the lookup files (e.g. "en-US" or "de-DE").
 * @param version       Version of the lookup files.
 * @param dictionaryUrl URL to the lookup file containing the dictionary words.
 * @param passwordsUrl  URL to the lookup file containing the common passwords.
 */
@Serializable
internal data class RestLookupFileDto(
    @SerialName("lang") val language: String,
    @SerialName("version") val version: Int,
    @SerialName("dictionary") val dictionaryUrl: String,
    @SerialName("passwords") val passwordsUrl: String
)
