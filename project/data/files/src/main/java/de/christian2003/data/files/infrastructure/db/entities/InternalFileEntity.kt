package de.christian2003.data.files.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


/**
 * Database entity for the internal file lookup.
 *
 * @param internalName              Internal name of the file from the internal filesystem
 *                                  (e.g. "abc123def456.enc").
 * @param encryptedActualFileName   Bytes of the encrypted actual file name (e.g. "Lawsuit.pdf").
 * @param createdAt                 Timestamp at which the file was created.
 * @param editedAt                  Timestamp at which the file was last edited.
 * @param accessedAt                Timestamp at which the file was last accessed (e.g. viewed).
 * @param size                      Size of the file in bytes.
 */
@Entity("files_lookup")
data class InternalFileEntity(

    @PrimaryKey
    @ColumnInfo(name = "internalName")
    val internalName: String,

    @ColumnInfo(name = "actualFileName")
    val encryptedActualFileName: ByteArray,

    @ColumnInfo(name = "createdAt")
    val createdAt: LocalDateTime,

    @ColumnInfo(name = "editedAt")
    val editedAt: LocalDateTime,

    @ColumnInfo(name = "accessedAt")
    val accessedAt: LocalDateTime,

    @ColumnInfo(name = "size")
    val size: Long

) {

    //Auto-generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InternalFileEntity

        if (size != other.size) return false
        if (internalName != other.internalName) return false
        if (!encryptedActualFileName.contentEquals(other.encryptedActualFileName)) return false
        if (createdAt != other.createdAt) return false
        if (editedAt != other.editedAt) return false
        if (accessedAt != other.accessedAt) return false

        return true
    }


    //Auto-generated
    override fun hashCode(): Int {
        var result = size.hashCode()
        result = 31 * result + internalName.hashCode()
        result = 31 * result + encryptedActualFileName.contentHashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + editedAt.hashCode()
        result = 31 * result + accessedAt.hashCode()
        return result
    }

}
