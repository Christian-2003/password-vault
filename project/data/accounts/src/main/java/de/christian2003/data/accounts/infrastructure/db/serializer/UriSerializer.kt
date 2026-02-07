package de.christian2003.data.accounts.infrastructure.db.serializer

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import androidx.core.net.toUri


/**
 * Serializer to serialize an Uri instance with the Kotlin serialization API.
 */
@Serializer(forClass = Uri::class)
class UriSerializer: KSerializer<Uri> {

    /**
     * Serializes the Uri instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized uri.
     * @param value     Uri to serialize.
     */
    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }


    /**
     * Deserializes an Uri instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized Uri.
     */
    override fun deserialize(decoder: Decoder): Uri {
        return try {
            decoder.decodeString().toUri()
        } catch (_: Exception) {
            Uri.fromParts("about", "blank", null)
        }
    }

}
