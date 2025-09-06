package de.christian2003.passwordvault.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.nio.file.attribute.FileAttribute
import java.time.LocalDateTime
import kotlin.uuid.Uuid

@Entity(
    tableName = "details",
    foreignKeys = [
        ForeignKey(
            entity = EntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entry"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class DetailEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Uuid,

    @ColumnInfo(name = "entry")
    var entry: Uuid,

    @ColumnInfo(name = "payload")
    var payload: ByteArray,

    @ColumnInfo(name = "createdAt")
    var createdAt: LocalDateTime,

    @ColumnInfo(name = "editedAt")
    var editedAt: LocalDateTime,

    @ColumnInfo(name = "isObfuscated")
    var isObfuscated: Boolean,

    @ColumnInfo(name = "isVisible")
    var isVisible: Boolean

) {

    override fun hashCode(): Int {
        return id.hashCode()
    }


    override fun equals(other: Any?): Boolean {
        return (other is DetailEntity) && (other.id == this.id)
    }

}
