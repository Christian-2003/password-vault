package de.christian2003.feature.export.infrastructure.backup.v2.dto

import java.time.LocalDateTime
import kotlin.uuid.Uuid


/**
 * DTO for the entries (i.e. accounts) of a V2 backup.
 *
 * @param id                    ID of the entry.
 * @param name                  Display name of the entry.
 * @param description           Description of the entry.
 * @param created               Metadata: When the entry was created.
 * @param edited                Metadata: When the entry was last edited.
 * @param isVisible             Metadata: Whether the entry is visible.
 * @param isAddedAutomatically  Metadata: Whether the entry was added automatially through autofill service.
 * @param tagIds                List of tags of the entry.
 * @param packages              List of package names of the entry.
 */
internal data class V2BackupEntryDto(
    val id: Uuid,
    val name: String,
    val description: String?,
    val created: LocalDateTime,
    val edited: LocalDateTime,
    val isVisible: Boolean,
    val isAddedAutomatically: Boolean,
    val tagIds: List<Uuid>,
    val packages: List<String>
)
