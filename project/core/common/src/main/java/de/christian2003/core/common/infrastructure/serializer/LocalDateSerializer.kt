package de.christian2003.core.common.infrastructure.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate


/**
 * Serializer for LocalDate instances which (de)serializes instances to / from strings in
 * ISO-8601 format.
 */
@Serializer(LocalDate::class)
class LocalDateSerializer: KSerializer<LocalDate> {

    /**
     * Serializes the local date instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized local date.
     * @param value     Local date to serialize.
     */
    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }


    /**
     * Deserializes a local date instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized local date.
     */
    override fun deserialize(decoder: Decoder): LocalDate {
        return try {
            LocalDate.parse(decoder.decodeString())
        } catch (_: Exception) {
            LocalDate.now()
        }
    }

}
