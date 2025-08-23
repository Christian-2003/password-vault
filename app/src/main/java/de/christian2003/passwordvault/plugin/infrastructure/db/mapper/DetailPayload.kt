package de.christian2003.passwordvault.plugin.infrastructure.db.mapper

import de.christian2003.passwordvault.domain.entry.DetailIcon
import de.christian2003.passwordvault.domain.entry.DetailType
import de.christian2003.passwordvault.plugin.infrastructure.db.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


/**
 * Models the payload for the detail, that is being serialized into CBOR.
 */
@Serializable
class DetailPayload (

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

    /**
     * Whether the detail content is obfuscated.
     */
    @SerialName("isObfuscated")
    val isObfuscated: Boolean = false,

    /**
     * Whether the detail content is visible by default or hidden beneath the "Show more details"
     * button.
     */
    @SerialName("isVisible")
    val isVisible: Boolean = true,

    /**
     * Date time on which the detail was created. This is for statistical purposes.
     */
    @SerialName("created")
    @Serializable(with = LocalDateTimeSerializer::class)
    val created: LocalDateTime = LocalDateTime.now(),

    /**
     * Date time on which the detail was edited the last time. This is for statistical purposes.
     */
    @SerialName("isEdited")
    @Serializable(with = LocalDateTimeSerializer::class)
    val edited: LocalDateTime = LocalDateTime.now()

)
