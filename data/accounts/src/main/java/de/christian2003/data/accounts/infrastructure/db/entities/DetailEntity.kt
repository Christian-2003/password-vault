package de.christian2003.data.accounts.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import kotlin.uuid.Uuid


@Entity(
    tableName = "details",
    indices = [
        Index(value = ["id", "account"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class DetailEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Uuid,

    @ColumnInfo(name = "account")
    val account: Uuid,

    @ColumnInfo(name = "payload")
    val payload: ByteArray,

    @ColumnInfo(name = "createdAt")
    val createdAt: LocalDateTime,

    @ColumnInfo(name = "editedAt")
    val editedAt: LocalDateTime,

    @ColumnInfo(name = "isObfuscated")
    val isObfuscated: Boolean,

    @ColumnInfo(name = "isVisible")
    val isVisible: Boolean

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DetailEntity

        if (isObfuscated != other.isObfuscated) return false
        if (isVisible != other.isVisible) return false
        if (id != other.id) return false
        if (account != other.account) return false
        if (!payload.contentEquals(other.payload)) return false
        if (createdAt != other.createdAt) return false
        if (editedAt != other.editedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var hash = isObfuscated.hashCode()
        hash = 31 * hash + isVisible.hashCode()
        hash = 31 * hash + id.hashCode()
        hash = 31 * hash + account.hashCode()
        hash = 31 * hash + payload.contentHashCode()
        hash = 31 * hash + createdAt.hashCode()
        hash = 31 * hash + editedAt.hashCode()
        return hash
    }

}
