package de.christian2003.passwordvault.infrastructure.helppages.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Root DTO for the help pages REST API.
 *
 * @param pages List of help pages.
 */
@Serializable
internal data class HelpPagesRootDto(

    @SerialName("tutorials")
    val pages: List<HelpPageRestDto>

)
