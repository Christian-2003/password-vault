package de.christian2003.feature.accounts.models.dto

import de.christian2003.data.accounts.domain.entities.Tag


/**
 * Mapper maps the domain model 'Tag' to the DTO for the UI.
 *
 * For the reason on why DTOs are needed for the UI, see the documentation of "TagUiDto".
 */
internal class TagUiMapper {

    /**
     * Maps the DTO that is passed as argument to the domain model 'Tag'.
     *
     * @param dto   DTO to map to the domain model 'Tag'.
     * @return      Domain model 'Tag'.
     */
    fun toDomain(dto: TagUiDto): Tag {
        return Tag(
            id = dto.id,
            name = dto.name,
            metadata = dto.metadata
        )
    }


    /**
     * Maps the domain model 'Tag' that is passed as argument to the DTO.
     *
     * @param domain    Domain model 'Tag' to map to the DTO.
     * @return          DTO.
     */
    fun toDto(domain: Tag): TagUiDto {
        return TagUiDto(
            id = domain.id,
            name = domain.name,
            metadata = domain.metadata
        )
    }

}
