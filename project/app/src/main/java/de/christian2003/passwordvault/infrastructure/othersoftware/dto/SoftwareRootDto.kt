package de.christian2003.passwordvault.infrastructure.othersoftware.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Root DTO for the JSON response returned from the server when querying the other software to
 * advertise inside Password Vault.
 *
 * @param version   Version of the JSON response.
 * @param software  List of other software to advertise.
 */
@Serializable
internal data class SoftwareRootDto(

    @SerialName("version")
    val version: Int,

    @SerialName("android")
    val software: List<SoftwareItemDto>

)
