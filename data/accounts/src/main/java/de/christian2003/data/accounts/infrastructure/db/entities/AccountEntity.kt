package de.christian2003.data.accounts.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import kotlin.uuid.Uuid


@Entity(
    tableName = "accounts",
    indices = [
        Index(value = ["id"])
    ]
)
internal data class AccountEntity (

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Uuid,

    @ColumnInfo(name = "payload")
    val payload: ByteArray,

    @ColumnInfo(name = "createdAt")
    val createdAt: LocalDateTime,

    @ColumnInfo(name = "editedAt")
    val editedAt: LocalDateTime,

    @ColumnInfo(name = "accessedAt")
    val accessedAt: LocalDateTime

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountEntity

        if (id != other.id) return false
        if (!payload.contentEquals(other.payload)) return false
        if (createdAt != other.createdAt) return false
        if (editedAt != other.editedAt) return false
        if (accessedAt != other.accessedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + editedAt.hashCode()
        result = 31 * result + accessedAt.hashCode()
        return result
    }

}
