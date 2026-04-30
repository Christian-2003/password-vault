package de.christian2003.feature.export.infrastructure.backup.v3.dto

import de.christian2003.feature.export.infrastructure.backup.v3.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.uuid.Uuid


@Serializable
internal data class V3BackupTagDto(

    @SerialName("id")
    val id: Uuid,

    @SerialName("name")
    val name: String,

    @SerialName("createdAt")
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,

    @SerialName("editedAt")
    @Serializable(LocalDateTimeSerializer::class)
    val editedAt: LocalDateTime

)
