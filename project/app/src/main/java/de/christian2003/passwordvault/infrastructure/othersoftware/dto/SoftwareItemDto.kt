package de.christian2003.passwordvault.infrastructure.othersoftware.dto

import android.net.Uri
import de.christian2003.core.common.infrastructure.serializer.UriSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * DTO used to deserialize the JSON response returned from the webserver.
 *
 * @param packageName   Package name if this is an Android app.
 * @param websiteUrl    URL for the website advertising the software.
 * @param softwareName  Display name of the software.
 * @param iconUrl       URL to the software icon SVG.
 */
@Serializable
internal data class SoftwareItemDto(

    @SerialName("package")
    val packageName: String,

    @SerialName("url")
    @Serializable(with = UriSerializer::class)
    val websiteUrl: Uri,

    @SerialName("name")
    val softwareName: String,

    @SerialName("icon")
    @Serializable(with = UriSerializer::class)
    val iconUrl: Uri

)
