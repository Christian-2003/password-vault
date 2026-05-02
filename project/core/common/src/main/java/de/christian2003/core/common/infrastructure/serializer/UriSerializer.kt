package de.christian2003.core.common.infrastructure.serializer

import android.net.Uri
import androidx.core.net.toUri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * Serializer for Uri instances which (de)serializes instances to / from strings.
 */
@Serializer(Uri::class)
class UriSerializer: KSerializer<Uri> {

    /**
     * Serializes the Uri instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized Uri.
     * @param value     Uri to serialize.
     */
    override fun serialize(encoder: Encoder, value: Uri) {
        encoder.encodeString(value.toString())
    }


    /**
     * Deserializes a Uri instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized Uri.
     */
    override fun deserialize(decoder: Decoder): Uri {
        return try {
            decoder.decodeString().toUri()
        } catch (_: Exception) {
            "about:blank".toUri()
        }
    }

}
