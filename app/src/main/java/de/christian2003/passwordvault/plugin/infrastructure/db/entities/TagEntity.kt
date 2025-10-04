package de.christian2003.passwordvault.plugin.infrastructure.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import kotlin.uuid.Uuid


@Entity("tags")
data class TagEntity(

    @PrimaryKey
    @ColumnInfo("id")
    val id: Uuid,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("createdAt")
    val createdAt: LocalDateTime,

    @ColumnInfo("editedAt")
    val editedAt: LocalDateTime

)
