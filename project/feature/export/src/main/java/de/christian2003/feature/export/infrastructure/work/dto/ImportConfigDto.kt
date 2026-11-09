package de.christian2003.feature.export.infrastructure.work.dto

import android.net.Uri
import de.christian2003.core.common.infrastructure.serializer.UriSerializer
import de.christian2003.feature.export.domain.entities.ImportOptions
import kotlinx.serialization.Serializable


/**
 * Data transfer object for an ImportConfig. Can be serialized to JSON, which is required because
 * Android work manager only accepts no custom types.
 *
 * @param accountsImportOptions Import options for accounts.
 * @param importFile            URI of the imported file.
 * @param decryptionKeySeed     Seed for the encryption key (if required).
 */
internal data class ImportConfigDto(
    val accountsImportOptions: Int,
    @Serializable(UriSerializer::class) val importFile: Uri,
    val decryptionKeySeed: CharArray? = null
) {

    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImportConfigDto

        if (importFile != other.importFile) return false
        if (accountsImportOptions != other.accountsImportOptions) return false
        if (!decryptionKeySeed.contentEquals(other.decryptionKeySeed)) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = importFile.hashCode()
        result = 31 * result + accountsImportOptions.hashCode()
        result = 31 * result + (decryptionKeySeed?.contentHashCode() ?: 0)
        return result
    }

}
