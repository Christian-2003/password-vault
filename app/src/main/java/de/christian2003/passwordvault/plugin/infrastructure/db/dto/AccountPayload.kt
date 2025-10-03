package de.christian2003.passwordvault.plugin.infrastructure.db.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Models the payload for the entry, that is being serialized into CBOR.
 */
@Serializable
class AccountPayload (

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

)
