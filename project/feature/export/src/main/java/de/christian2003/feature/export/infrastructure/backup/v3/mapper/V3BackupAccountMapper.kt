package de.christian2003.feature.export.infrastructure.backup.v3.mapper

import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.AccountMetadata
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailMetadata
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupAccountDto
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupDetailDto
import de.christian2003.feature.export.infrastructure.backup.v3.dto.V3BackupTargetDto
import javax.inject.Inject
import kotlin.uuid.Uuid


/**
 * Mapper to map a domain account to it's backup DTO and vice versa.
 */
internal class V3BackupAccountMapper @Inject constructor() {

    /**
     * Converts the specified DTO to it's domain instance.
     *
     * @param dto       DTO to convert.
     * @param allTags   List of all tags.
     * @return          Converted domain instance.
     */
    fun toDomain(dto: V3BackupAccountDto, allTags: List<Tag>): Account {
        //Targets:
        val targets: List<Target> = dto.targets.map { targetDto ->
            Target(
                name = targetDto.name,
                url = targetDto.url,
                id = targetDto.id,
                faviconFile = targetDto.faviconFile
            )
        }

        //Details:
        val details: List<Detail> = dto.details.map { detailDto ->
            Detail(
                name = detailDto.name,
                content = detailDto.content,
                id = detailDto.id,
                type = detailDto.type,
                icon = detailDto.icon,
                metadata = DetailMetadata(
                    createdAt = detailDto.createdAt,
                    editedAt = detailDto.editedAt,
                    isObfuscated = detailDto.isObfuscated,
                    isVisible = detailDto.isVisible
                )
            )
        }

        //Tags:
        val tags: MutableList<Tag> = mutableListOf()
        dto.tags.forEach { tagId ->
            val tag: Tag? = allTags.firstOrNull { it.id == tagId }
            if (tag != null) {
                tags.add(tag)
            }
        }

        //Final result:
        val domain = Account(
            descriptor = AccountDescriptor(
                name = dto.name,
                description = dto.description,
                id = dto.id,
                targets = targets
            ),
            details = details,
            tags = tags,
            metadata = AccountMetadata(
                createdAt = dto.createdAt,
                editedAt = dto.editedAt,
                accessedAt = dto.accessedAt
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
    fun toDto(domain: Account): V3BackupAccountDto {
        //Targets:
        val targets: List<V3BackupTargetDto> = domain.targets.map { target ->
            V3BackupTargetDto(
                name = target.name,
                url = target.url,
                id = target.id,
                faviconFile = target.faviconFile
            )
        }

        //Details:
        val details: List<V3BackupDetailDto> = domain.details.map { detail ->
            V3BackupDetailDto(
                id = detail.id,
                name = detail.name,
                content = detail.content,
                type = detail.type,
                icon = detail.icon,
                createdAt = detail.metadata.createdAt,
                editedAt = detail.metadata.editedAt,
                isObfuscated = detail.metadata.isObfuscated,
                isVisible = detail.metadata.isVisible
            )
        }

        //Tags:
        val tags: List<Uuid> = domain.tags.map { tag -> tag.id }

        //Final result:
        val dto = V3BackupAccountDto(
            id = domain.descriptor.id,
            name = domain.descriptor.name,
            description = domain.descriptor.description,
            createdAt = domain.metadata.createdAt,
            editedAt = domain.metadata.editedAt,
            accessedAt = domain.metadata.accessedAt,
            details = details,
            targets = targets,
            tags = tags
        )

        return dto
    }

}
