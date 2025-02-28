package de.christian2003.accounts.database

import androidx.room.TypeConverter
import java.util.UUID


/**
 * Class implements a converter for UUID instances.
 * This converter is required in order to use UUIDs within room databases.
 *
 * @author  Christian-2003
 * @since   3.8.0
 */
class UUIDConverter {

    /**
     * Converts the UUID specified to it's string representation.
     *
     * @param uuid  UUID to convert into it's string representation.
     * @return      String representation of the UUID.
     */
    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }


    /**
     * Converts the string representation of a UUID to a UUID.
     *
     * @param uuid  String representation of the UUID to convert.
     * @return      UUID converted from it's string representation.
     */
    @TypeConverter
    fun toUUID(uuid: String): UUID {
        return UUID.fromString(uuid)
    }

}
