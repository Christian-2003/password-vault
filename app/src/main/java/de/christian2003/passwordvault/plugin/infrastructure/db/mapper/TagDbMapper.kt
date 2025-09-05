package de.christian2003.passwordvault.plugin.infrastructure.db.mapper

import de.christian2003.passwordvault.domain.model.tag.Tag
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TagEntity


class TagDbMapper {

    fun toDomain(entity: TagEntity): Tag {
        return Tag(
            id = entity.id,
            name = entity.name
        )
    }


    fun toEntity(domain: Tag): TagEntity {
        return TagEntity(
            id = domain.id,
            name = domain.name
        )
    }

}
