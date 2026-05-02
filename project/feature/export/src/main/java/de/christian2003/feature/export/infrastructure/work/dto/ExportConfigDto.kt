package de.christian2003.feature.export.infrastructure.work.dto

import android.net.Uri
import de.christian2003.core.common.infrastructure.serializer.UriSerializer
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
internal data class ExportConfigDto(
    val accounts: Set<Uuid>,
    val files: Set<String>,
    @Serializable(UriSerializer::class) val exportDestination: Uri,
    val encryptionKeySeed: CharArray? = null
) {

    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExportConfigDto

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
