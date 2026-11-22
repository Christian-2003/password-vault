package de.christian2003.passwordvault.infrastructure.legal.dto

import de.christian2003.core.common.infrastructure.serializer.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate


/**
 * DTO for a legal page from the REST API.
 *
 * @param version   Version of the legal document.
 * @param valid     Date at which the legal document takes effect.
 * @param items     Localized information about the page.
 */
@Serializable
internal data class LegalPageRestDto(

    @SerialName("version")
    val version: Int = -1,

    @SerialName("valid")
    @Serializable(with = LocalDateSerializer::class)
    val valid: LocalDate = LocalDate.MIN,

    @SerialName("pages")
    val items: List<LegalPageItemRestDto> = emptyList()

)
