package de.christian2003.feature.export.infrastructure.backup.v3.dto

import de.christian2003.data.accounts.domain.entities.DetailIcon
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.core.common.infrastructure.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.uuid.Uuid


@Serializable
internal data class V3BackupDetailDto(

    @SerialName("id")
    val id: Uuid,

    @SerialName("name")
    val name: String,

    @SerialName("content")
    val content: String,

    @SerialName("type")
    val type: DetailType,

    @SerialName("icon")
    val icon: DetailIcon?,

    @SerialName("createdAt")
    @Serializable(LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,

    @SerialName("editedAt")
    @Serializable(LocalDateTimeSerializer::class)
    val editedAt: LocalDateTime,

    @SerialName("isObfuscated")
    val isObfuscated: Boolean,

    @SerialName("isVisible")
    val isVisible: Boolean

)
