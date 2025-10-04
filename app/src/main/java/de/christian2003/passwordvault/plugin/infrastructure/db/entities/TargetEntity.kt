package de.christian2003.passwordvault.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid


@Entity(
    tableName = "targets",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["entry"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TargetEntity(

    @PrimaryKey
    @ColumnInfo("id")
    val id: Uuid,

    @ColumnInfo("entry")
    val entry: Uuid,

    @ColumnInfo("payload")
    val payload: ByteArray

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TargetEntity

        if (id != other.id) return false
        if (entry != other.entry) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + entry.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }

}
