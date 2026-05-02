package de.christian2003.feature.export.domain.entities

import android.net.Uri
import kotlin.uuid.Uuid


/**
 * Value object contains the configuration for creating an export.
 *
 * @param accounts          IDs of the accounts to include in the export.
 * @param files             Internal names of the files to include.
 * @param exportDestination Destination URI where to create the exported file.
 * @param encryptionKeySeed Seed for the encryption key. This is only required if the backup is
 *                          encrypted. Otherwise, null can be passed.
 */
internal data class ExportConfig(
    val accounts: Set<Uuid>,
    val files: Set<String>,
    val exportDestination: Uri,
    val encryptionKeySeed: CharArray? = null
) {

    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExportConfig

        if (accounts != other.accounts) return false
        if (files != other.files) return false
        if (exportDestination != other.exportDestination) return false
        if (!encryptionKeySeed.contentEquals(other.encryptionKeySeed)) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = accounts.hashCode()
        result = 31 * result + files.hashCode()
        result = 31 * result + exportDestination.hashCode()
        result = 31 * result + (encryptionKeySeed?.contentHashCode() ?: 0)
        return result
    }

}
