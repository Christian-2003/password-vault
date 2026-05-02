package de.christian2003.feature.export.infrastructure.backup.v3.dto

import de.christian2003.core.common.infrastructure.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.uuid.Uuid


@Serializable
internal data class V3BackupAccountDto(

    @SerialName("id")
    val id: Uuid,

    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String,

    @SerialName("createdAt")
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,

    @SerialName("editedAd")
    @Serializable(LocalDateTimeSerializer::class)
    val editedAt: LocalDateTime,

    @SerialName("accessedAt")
    @Serializable(LocalDateTimeSerializer::class)
    val accessedAt: LocalDateTime,

    @SerialName("details")
    val details: List<V3BackupDetailDto>,

    @SerialName("targets")
    val targets: List<V3BackupTargetDto>,

    @SerialName("tags")
    val tags: List<Uuid>

)
