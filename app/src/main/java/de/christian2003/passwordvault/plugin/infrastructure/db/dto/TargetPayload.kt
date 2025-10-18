package de.christian2003.passwordvault.plugin.infrastructure.db.dto

import android.net.Uri
import de.christian2003.passwordvault.plugin.infrastructure.db.serializer.UriSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Models the payload for the target that is serialized using CBOR.
 */
@Serializable
class TargetPayload(

    /**
     * Name of the target.
     */
    @SerialName("name")
    val name: String,

    /**
     * URL of the target.
     */
    @SerialName("url")
    @Serializable(with = UriSerializer::class)
    val url: Uri,

    /**
     * Favicon file of the target.
     */
    @SerialName("favicon")
    val faviconFile: String?

)
