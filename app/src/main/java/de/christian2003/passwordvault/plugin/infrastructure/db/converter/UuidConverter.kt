package de.christian2003.passwordvault.plugin.infrastructure.db.converter

import androidx.room.TypeConverter
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


/**
 * Converter for Room database to convert a UUID field into a byte array and vice versa.
 */
class UuidConverter {

    /**
     * Converts the passed UUID into a byte array.
     *
     * @param value UUID to convert into a byte array.
     * @return      Byte array converted from the passed UUID.
     */
    @TypeConverter
    fun fromUuid(value: Uuid): ByteArray {
        return value.toByteArray()
    }


    /**
     * Converts the byte array representing a UUID into a UUID instance.
     *
     * @param value Byte array to convert into a UUID.
     * @return      UUID converted from the passed byte array.
     */
    @TypeConverter
    fun toUuid(value: ByteArray): Uuid {
        return Uuid.fromByteArray(value)
    }

}
