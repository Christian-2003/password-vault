package de.christian2003.feature.export.infrastructure.backup.v2.dto

import kotlin.uuid.Uuid


/**
 * DTO for the tags of a V2 backup.
 *
 * @param id    ID of the tag.
 * @param name  Display name of the tag.
 */
internal data class V2BackupTagDto(
    val id: Uuid,
    val name: String
)
