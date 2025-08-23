package de.christian2003.passwordvault.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity("details")
class DetailEntity(

    @PrimaryKey
    @ColumnInfo("id")
    var id: Uuid,

    @ColumnInfo("entry")
    var entry: Uuid,

    @ColumnInfo("payload")
    var payload: ByteArray

) {

    override fun hashCode(): Int {
        return id.hashCode()
    }


    override fun equals(other: Any?): Boolean {
        return (other is DetailEntity) && (other.id == this.id)
    }

}
