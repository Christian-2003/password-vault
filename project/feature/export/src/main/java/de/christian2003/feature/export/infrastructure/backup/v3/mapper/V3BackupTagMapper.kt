package de.christian2003.feature.export.infrastructure.backup.v3.mapper

import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.entities.TagMetadata
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupTagDto
import javax.inject.Inject


/**
 * Mapper to map a domain tag to it's backup DTO and vice versa.
 */
internal class V3BackupTagMapper @Inject constructor() {

    /**
     * Converts the specified DTO to it's domain instance.
     *
     * @param dto   DTO to convert.
     * @return      Converted domain instance.
     */
    fun toDomain(dto: V3BackupTagDto): Tag {
        val domain = Tag(
            id = dto.id,
            name = dto.name,
            metadata = TagMetadata(
                createdAt = dto.createdAt,
                editedAt = dto.editedAt
            )
        )

        return domain
    }


    /**
     * Converts the specified domain instance to it's DTO.
     *
     * @param domain    Domain instance to convert.
     * @return          Converted DTO.
     */
    fun toDto(domain: Tag): V3BackupTagDto {
        val dto = V3BackupTagDto(
            id = domain.id,
            name = domain.name,
            createdAt = domain.metadata.createdAt,
            editedAt = domain.metadata.editedAt
        )

        return dto
    }

}
