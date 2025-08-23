package de.christian2003.passwordvault.plugin.infrastructure.db.mapper

import de.christian2003.passwordvault.plugin.infrastructure.db.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


/**
 * Models the payload for the entry, that is being serialized into CBOR.
 */
@Serializable
class EntryPayload (

    /**
     * Name for the entry, which is set by the user.
     */
    @SerialName("name")
    val name: String = "",

    /**
     * Description for the entry, which is set by the user.
     */
    @SerialName("description")
    val description: String = "",

    /**
     * Date time on which the entry was created. This is for statistical purposes.
     */
    @SerialName("created")
    @Serializable(with = LocalDateTimeSerializer::class)
    val created: LocalDateTime = LocalDateTime.now(),

    /**
     * Date time on which the entry was edited the last time. This is for statistical purposes.
     */
    @SerialName("edited")
    @Serializable(with = LocalDateTimeSerializer::class)
    val edited: LocalDateTime = LocalDateTime.now()

)
