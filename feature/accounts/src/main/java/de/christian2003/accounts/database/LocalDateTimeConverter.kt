package de.christian2003.accounts.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset


class LocalDateTimeConverter {

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime): Long {
        return localDateTime.toEpochSecond(ZoneOffset.UTC)
    }


    @TypeConverter
    fun toLocalDateTime(localDateTime: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(localDateTime, 0, ZoneOffset.UTC)
    }

}
