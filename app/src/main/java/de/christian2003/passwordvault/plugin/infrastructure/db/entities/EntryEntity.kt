package de.christian2003.passwordvault.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalUuidApi::class)
@Entity(tableName = "entries")
class EntryEntity (

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Uuid,

    @ColumnInfo(name = "payload")
    var payload: ByteArray

) {

    override fun hashCode(): Int {
        return id.hashCode()
    }


    override fun equals(other: Any?): Boolean {
        return (other is EntryEntity) && (other.id == this.id)
    }

}
