package de.christian2003.passwordvault.infrastructure.legal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Root DTO for the legal REST API.
 *
 * @param privacyPolicy     Legal page of the privacy policy.
 * @param termsOfService    Legal page for the terms of service.
 */
@Serializable
internal data class LegalRootRestDto(

    @SerialName("privacy")
    val privacyPolicy: LegalPageRestDto? = null,

    @SerialName("tos")
    val termsOfService: LegalPageRestDto? = null

)
