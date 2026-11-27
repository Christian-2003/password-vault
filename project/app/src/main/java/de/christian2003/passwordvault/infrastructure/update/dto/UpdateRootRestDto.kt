package de.christian2003.passwordvault.infrastructure.update.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Root DTO for the update REST API.
 *
 * @param packageName   Name of the package queried (Should be identical to this package's name).
 * @param version       Info about the newest release.
 */
@Serializable
internal data class UpdateRootRestDto(

    @SerialName("package")
    val packageName: String = "",

    @SerialName("version")
    val version: UpdateVersionRestDto? = null

)
