package de.christian2003.feature.export.infrastructure.backup.v3.dto

import de.christian2003.core.common.infrastructure.serializer.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.uuid.Uuid


/**
 * DTO for the accounts of a V3 backup.
 *
 * @param id            ID of the account.
 * @param name          Display name of the account.
 * @param description   Description of the account.
 * @param createdAt     Metadata: When the account was created.
 * @param editedAt      Metadata: When the account was last edited.
 * @param accessedAt    Metadata: When the account was last accessed.
 * @param details       List of details that belong to the account.
 * @param targets       List of autofill targets of the account.
 * @param tags          List of IDs for the tags assigned to the account.
 */
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
