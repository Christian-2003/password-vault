package de.christian2003.feature.export.infrastructure.backup.v3.dto

import de.christian2003.data.accounts.domain.entities.DetailIcon
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.core.common.infrastructure.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.uuid.Uuid


/**
 * DTO for the details of a V3 backup.
 *
 * @param id            ID of the detail.
 * @param name          Display name of the detail.
 * @param content       Content of the detail.
 * @param type          Metadata: Autofill type of the detail.
 * @param icon          Metadata: Display icon of the detail. Null if no icon is specified.
 * @param createdAt     Metadata: When the detail was created.
 * @param editedAt      Metadata: When the detail was last edited.
 * @param isObfuscated  Metadata: Whether the detail content is obfuscated when displayed.
 * @param isVisible     Metadata: Whether the detail is visible when an account is displayed.
 */
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
