package de.christian2003.passwordvault.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import kotlin.uuid.Uuid


@Entity(tableName = "entries")
class AccountEntity (

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Uuid,

    @ColumnInfo(name = "payload")
    var payload: ByteArray,

    @ColumnInfo(name = "createdAt")
    var createdAt: LocalDateTime,

    @ColumnInfo(name = "editedAt")
    var editedAt: LocalDateTime,

    @ColumnInfo(name = "accessedAt")
    var accessedAt: LocalDateTime

) {

    override fun hashCode(): Int {
        return id.hashCode()
    }


    override fun equals(other: Any?): Boolean {
        return (other is AccountEntity) && (other.id == this.id)
    }

}
