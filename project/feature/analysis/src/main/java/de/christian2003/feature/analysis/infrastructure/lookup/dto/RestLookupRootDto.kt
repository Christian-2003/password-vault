package de.christian2003.feature.analysis.infrastructure.lookup.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * DTO contains the entire response from the REST endpoint to query the lookup data.
 *
 * @param metadata  Metadata of the response.
 * @param files     List of lookup files grouped by language.
 */
@Serializable
internal data class RestLookupRootDto(
    @SerialName("metadata") val metadata: RestLookupMetadataDto,
    @SerialName("languages") val files: List<RestLookupFileDto>
)
