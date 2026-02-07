package de.christian2003.data.accounts.infrastructure.db.converter

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter


/**
 * Converter for Room database to convert an Uri field into a string and vice versa.
 */
internal class UriConverter {

    /**
     * Converts the passed Uri into a string.
     *
     * @param value Uri to convert into a string.
     * @return      String converted from the passed Uri.
     */
    @TypeConverter
    fun fromUri(value: Uri): String {
        return value.toString()
    }


    /**
     * Converts the string representing an uri into a Uri instance.
     *
     * @param value String to convert into an Uri.
     * @return      Uri converted from the passed string.
     */
    @TypeConverter
    fun toUri(value: String): Uri {
        return try {
            value.toUri()
        } catch (_: Exception) {
            Uri.fromParts("about", "blank", null)
        }
    }

}
