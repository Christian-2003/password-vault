package de.christian2003.data.accounts.infrastructure.db.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid


@Entity(
    tableName = "targets",
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
data class TargetEntity(

    @PrimaryKey
    @ColumnInfo("id")
    val id: Uuid,

    @ColumnInfo("account")
    val account: Uuid,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("url")
    val url: Uri,

    @ColumnInfo("favicon")
    val faviconFile: String?

)
