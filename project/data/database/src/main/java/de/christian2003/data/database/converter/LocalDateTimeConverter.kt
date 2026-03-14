package de.christian2003.data.database.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Converter for Room database to convert a local date time field into a long and vice versa.
 */
internal class LocalDateTimeConverter {

    /**
     * Converts the passed local date time into a long.
     *
     * @param value Local date time to convert into a long.
     * @return      Long converted from the passed local date time.
     */
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): Long {
        return value.toEpochSecond(ZoneOffset.UTC)
    }


    /**
     * Converts the long representing a local date time into a LocalDateTime instance.
     *
     * @param value Long to convert into a local date time.
     * @return      Local date time converted from the passed long.
     */
    @TypeConverter
    fun toLocalDateTime(value: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC)
    }

}
