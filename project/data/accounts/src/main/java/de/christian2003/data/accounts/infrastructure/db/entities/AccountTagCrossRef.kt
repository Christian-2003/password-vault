package de.christian2003.data.accounts.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import kotlin.uuid.Uuid


@Entity(
    tableName = "accounts_tags_cross_ref",
    primaryKeys = ["account", "tag"],
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account"],
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

    @ColumnInfo("account")
    val account: Uuid,

    @ColumnInfo("tag")
    val tag: Uuid

)
