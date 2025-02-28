package de.christian2003.accounts.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Class implements a converter for LocalDateTime instances.
 * This converter is required to use LocalDateTime instances with room databases.
 *
 * @author  Christian-2003
 * @since   3.8.0
 */
class LocalDateTimeConverter {

    /**
     * Converts the local date time specified to it's epoch second.
     *
     * @param localDateTime Local date time whose epoch second to return.
     * @return              Epoch second of the local date time specified.
     */
    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime): Long {
        return localDateTime.toEpochSecond(ZoneOffset.UTC)
    }


    /**
     * Converts the epoch second specified to a local date time.
     *
     * @param localDateTime Epoch second to convert to a local date time.
     * @return              Local date time created from the epoch second specified.
     */
    @TypeConverter
    fun toLocalDateTime(localDateTime: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(localDateTime, 0, ZoneOffset.UTC)
    }

}
