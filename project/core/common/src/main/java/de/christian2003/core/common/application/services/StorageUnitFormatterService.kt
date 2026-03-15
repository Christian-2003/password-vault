package de.christian2003.core.common.application.services

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import de.christian2003.core.common.R


/**
 * Formatter service which formats storage units.
 *
 * @param context   Application context.
 */
class StorageUnitFormatterService @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    /**
     * Formats the provided size in bytes.
     *
     * @param bytes Bytes to format.
     * @return      Formatted bytes (e.g. "42 MB").
     */
    fun formatSize(bytes: Long): String {
        //Bytes:
        var adjusted: Long = bytes
        if (adjusted < 1_000) {
            return context.getString(R.string.unit_size_storage_b, adjusted)
        }

        //Kilobytes
        adjusted = adjusted / 1_000
        if (adjusted < 1_000) {
            return context.getString(R.string.unit_size_storage_kb, adjusted)
        }

        //Megabytes
        adjusted = adjusted / 1_000
        if (adjusted < 1_000) {
            return context.getString(R.string.unit_size_storage_mb, adjusted)
        }

        //Gigabytes
        adjusted = adjusted / 1_000
        return context.getString(R.string.unit_size_storage_gb, adjusted)
    }

}
