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
class TargetEntity(

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
        return (other is TargetEntity) && (other.id == this.id)
    }

}
