package de.christian2003.feature.export.infrastructure.backup.v2.dto

import java.time.LocalDateTime
import kotlin.uuid.Uuid


/**
 * DTO for details of V2 backups.
 *
 * @param id            ID of the detail.
 * @param entryId       ID of the entry of which this detail is a member.
 * @param name          Display name of the detail.
 * @param content       Content of the detail.
 * @param created       Metadata: When the detail was created.
 * @param edited        Metadata: When the detail was last edited.
 * @param typeId        Metadata: ID of the detail type (e.g. Email, Password or Username).
 * @param isVisible     Metadata: Whether the detail is visible.
 * @param isObfuscated  Metadata: Whether the detail is obfuscated.
 * @param isUsername    Metadata: Whether autofill treats the detail as username.
 * @param isPassword    Metadata: Whether autofill treats the detail as password.
 */
internal data class V2BackupDetailDto(
    val id: Uuid,
    val entryId: Uuid,
    val name: String,
    val content: String,
    val created: LocalDateTime,
    val edited: LocalDateTime,
    val typeId: Int,
    val isVisible: Boolean,
    val isObfuscated: Boolean,
    val isUsername: Boolean,
    val isPassword: Boolean
)
