package de.christian2003.feature.export.infrastructure.backup.v2.mapper

import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.entities.TagMetadata
import de.christian2003.feature.export.infrastructure.backup.v2.dto.V2BackupTagDto
import java.time.LocalDateTime
import javax.inject.Inject


/**
 * Mapper to map a DTO tag to its domain representation.
 * Other mapping direction (domain -> DTO) is not required since V2 only supports import.
 */
internal class V2BackupTagMapper @Inject constructor() {

    /**
     * Converts the specified DTO to its domain instance.
     *
     * @param dto   DTO to convert.
     * @return      Converted domain instance.
     */
    fun toDomain(dto: V2BackupTagDto): Tag {
        val domain = Tag(
            id = dto.id,
            name = dto.name,
            metadata = TagMetadata(
                createdAt = LocalDateTime.now(), //Not part of backup
                editedAt = LocalDateTime.now()   //Not part of backup
            )
        )

        return domain
    }

}
