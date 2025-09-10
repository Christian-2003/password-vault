package de.christian2003.passwordvault.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import kotlin.uuid.Uuid


@Entity("tags")
class TagEntity(

    @PrimaryKey
    @ColumnInfo("id")
    var id: Uuid,

    @ColumnInfo("name")
    var name: String,

    @ColumnInfo("createdAt")
    var createdAt: LocalDateTime,

    @ColumnInfo("editedAt")
    var editedAt: LocalDateTime

) {

    override fun hashCode(): Int {
        return id.hashCode()
    }


    override fun equals(other: Any?): Boolean {
        return (other is TagEntity) && (other.id == this.id)
    }

}
