package de.christian2003.feature.export.domain.entities

import android.net.Uri


/**
 * Value object contains the configuration for importing data.
 *
 * @param accountsImportOptions Describes what should happen if items shall be restored that already
 *                              exist in the app.
 * @param importFile            URI to the file from which to import the data.
 * @param decryptionKeySeed     Seed for the encryption key. This is only required if the export is
 *  *                           encrypted. Otherwise, null can be passed.
 */
internal data class ImportConfig(
    val accountsImportOptions: ImportOptions,
    val importFile: Uri,
    val decryptionKeySeed: CharArray? = null
) {

    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImportConfig

        if (accountsImportOptions != other.accountsImportOptions) return false
        if (importFile != other.importFile) return false
        if (!decryptionKeySeed.contentEquals(other.decryptionKeySeed)) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = accountsImportOptions.hashCode()
        result = 31 * result + importFile.hashCode()
        result = 31 * result + (decryptionKeySeed?.contentHashCode() ?: 0)
        return result
    }

}
