package de.christian2003.data.accounts.infrastructure.db.mapper

import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.entities.TagMetadata
import de.christian2003.data.accounts.infrastructure.db.entities.TagEntity


internal class TagDbMapper {

    fun toDomain(entity: TagEntity): Tag {
        return Tag(
            id = entity.id,
            name = entity.name,
            metadata = TagMetadata(
                createdAt = entity.createdAt,
                editedAt = entity.editedAt
            )
        )
    }


    fun toEntity(domain: Tag): TagEntity {
        return TagEntity(
            id = domain.id,
            name = domain.name,
            createdAt = domain.metadata.createdAt,
            editedAt = domain.metadata.editedAt
        )
    }

}
