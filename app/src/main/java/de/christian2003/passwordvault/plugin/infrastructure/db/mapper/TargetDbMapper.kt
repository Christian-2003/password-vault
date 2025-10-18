package de.christian2003.passwordvault.plugin.infrastructure.db.mapper

import de.christian2003.passwordvault.domain.model.target.Target
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.TargetEntity
import kotlin.uuid.Uuid


/**
 * Mapper maps the domain model 'Target' to the database entity.
 */
class TargetDbMapper() {

    /**
     * Maps the database entity that is passed as argument to the domain model 'Target'.
     *
     * @param entity    Database entity to map to the domain model 'Target'.
     * @return          Domain model 'Target'.
     */
    fun toDomain(entity: TargetEntity): Target {
        return Target(
            name = entity.name,
            url = entity.url,
            id = entity.id,
            faviconFile = entity.faviconFile
        )
    }


    /**
     * Maps the domain model 'Target' that is passed as argument to the database entity.
     *
     * @param domain    Domain model 'Target' to map to the database entity.
     * @param entry     ID of the entry to which the target is assigned.
     * @return          Database entity.
     */
    fun toEntity(domain: Target, entry: Uuid): TargetEntity {
        return TargetEntity(
            id = domain.id,
            account = entry,
            name = domain.name,
            url = domain.url,
            faviconFile = domain.faviconFile
        )
    }

}
