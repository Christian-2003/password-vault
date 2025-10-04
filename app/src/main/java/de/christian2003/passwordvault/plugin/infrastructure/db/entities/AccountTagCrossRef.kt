package de.christian2003.passwordvault.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import kotlin.uuid.Uuid


@Entity(
    tableName = "entries_tags_cross_ref",
    primaryKeys = ["entry", "tag"],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["entry"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AccountTagCrossRef(

    @ColumnInfo("entry")
    val entry: Uuid,

    @ColumnInfo("tag")
    val tag: Uuid

)
