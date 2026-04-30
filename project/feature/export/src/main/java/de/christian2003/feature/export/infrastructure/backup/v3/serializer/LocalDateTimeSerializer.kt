package de.christian2003.feature.export.infrastructure.backup.v3.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime


/**
 * Serializer for LocalDateTime instances which (de)serializes instances to / from strings in
 * ISO-8601 format.
 */
@Serializer(LocalDateTime::class)
internal class LocalDateTimeSerializer: KSerializer<LocalDateTime> {

    /**
     * Serializes the local date time instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized local date time.
     * @param value     Local date time to serialize.
     */
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }


    /**
     * Deserializes a local date time instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized local date time.
     */
    override fun deserialize(decoder: Decoder): LocalDateTime {
        return try {
            LocalDateTime.parse(decoder.decodeString())
        } catch (_: Exception) {
            LocalDateTime.now()
        }
    }

}
