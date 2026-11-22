package de.christian2003.passwordvault.infrastructure.helppages.dto

import android.net.Uri
import androidx.core.net.toUri
import de.christian2003.core.common.infrastructure.serializer.UriSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * DTO for a help page of the help pages REST API.
 *
 * @param minVersion    Min app version code (inclusive) where the help page applies.
 * @param maxVersion    Max app version code (inclusive) where the help page applies.
 * @param previewImage  URL of the help page preview image.
 * @param items         Help pages items containing the localized page information.
 */
@Serializable
internal data class HelpPageRestDto(

    @SerialName("minVersion")
    val minVersion: Long = 0,

    @SerialName("maxVersion")
    val maxVersion: Long = 0,

    @SerialName("preview")
    @Serializable(with = UriSerializer::class)
    val previewImage: Uri = "about:blank".toUri(),

    @SerialName("pages")
    val items: List<HelpPageItemRestDto> = emptyList()

)
