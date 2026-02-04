package de.christian2003.data.accounts.infrastructure.db.dto

import de.christian2003.data.accounts.domain.entities.DetailIcon
import de.christian2003.data.accounts.domain.entities.DetailType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Models the payload for the detail, that is being serialized into CBOR.
 */
@Serializable
internal class DetailPayload (

    /**
     * Name of the detail.
     */
    @SerialName("name")
    val name: String = "",

    /**
     * Content of the detail.
     */
    @SerialName("content")
    val content: String = "",

    /**
     * Type of the detail.
     */
    @SerialName("type")
    val type: DetailType = DetailType.TEXT,

    /**
     * Icon of the detail. This can be null. In this case, the default icon of the detail type is
     * used.
     */
    @SerialName("icon")
    val icon: DetailIcon? = null,

)
