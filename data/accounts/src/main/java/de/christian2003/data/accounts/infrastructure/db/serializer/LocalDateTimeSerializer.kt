package de.christian2003.data.accounts.infrastructure.db.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Serializer to serialize a LocalDateTime instance with the Kotlin serialization API.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDateTime::class)
internal class LocalDateTimeSerializer: KSerializer<LocalDateTime> {

    /**
     * Serializes the local date time instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized local date time.
     * @param value     Local date time to serialize.
     */
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeLong(value.toEpochSecond(ZoneOffset.UTC))
    }


    /**
     * Deserializes a local date time instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized local date time.
     */
    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.ofEpochSecond(decoder.decodeLong(), 0, ZoneOffset.UTC)
    }

}
