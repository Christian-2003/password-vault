package de.christian2003.core.common.infrastructure.serializer

import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Locale


/**
 * Serializer for Locale instances which (de)serializes instances to / from strings.
 */
@Serializer(Locale::class)
class LocaleSerializer {

    /**
     * Serializes the Locale instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized Locale.
     * @param value     Locale to serialize.
     */
    override fun serialize(encoder: Encoder, value: Locale) {
        encoder.encodeString(value.toLanguageTag())
    }


    /**
     * Deserializes a Locale instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized Locale.
     */
    override fun deserialize(decoder: Decoder): Locale {
        return try {
            Locale.forLanguageTag(decoder.decodeString())
        } catch (_: Exception) {
            Locale.getDefault()
        }
    }

}
