package de.christian2003.feature.analysis.infrastructure.lookup.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * DTO contains the metadata of the response from the REST endpoint to query the lookup data.
 *
 * @param version       Version of the response format.
 * @param serverTime    Server time.
 */
@Serializable
internal data class RestLookupMetadataDto(
    @SerialName("version") val version: Int,
    @SerialName("servertime") val serverTime: Long
)
