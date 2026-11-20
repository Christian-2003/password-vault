package de.christian2003.feature.export.infrastructure.backup.v2.mapper

import de.christian2003.data.accounts.domain.entities.Account
import de.christian2003.data.accounts.domain.entities.AccountDescriptor
import de.christian2003.data.accounts.domain.entities.AccountMetadata
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.Tag
import de.christian2003.data.accounts.domain.entities.Target
import de.christian2003.data.accounts.domain.services.PackageFingerprintService
import de.christian2003.feature.export.infrastructure.backup.v2.dto.V2BackupEntryDto
import java.time.LocalDateTime
import javax.inject.Inject


/**
 * Mapper to map a DTO entry to its domain representation (i.e. account).
 * Other mapping direction (domain -> DTO) is not required since V2 only supports import.
 */
internal class V2BackupAccountMapper @Inject constructor(
    private val packageFingerprintService: PackageFingerprintService
) {

    /**
     * Converts the specified DTO to its domain instance.
     *
     * @param dto   DTO to convert.
     * @return      Converted domain instance.
     */
    fun toDomain(dto: V2BackupEntryDto, details: List<Detail>, allTags: List<Tag>): Account {
        val targets: List<Target> = dto.packages.mapNotNull { packageNameToTarget(it) }

        val tags: List<Tag> = allTags.filter { it.id in dto.tagIds }

        val account = Account(
            descriptor = AccountDescriptor(
                name = dto.name,
                description = dto.description ?: "",
                id = dto.id,
                targets = targets
            ),
            details = details,
            tags = tags,
            metadata = AccountMetadata(
                createdAt = dto.created,
                editedAt = dto.edited,
                accessedAt = LocalDateTime.now() //Not part of V2 backup
            )
        )

        return account
    }


    /**
     * Generates a Target from the provided package name.
     *
     * @param packageName   Android package name.
     * @return              Target. May be null if no app with the specified package name is installed.
     */
    private fun packageNameToTarget(packageName: String): Target? {
        val target: Target? = Target.createAndroidTarget(packageName, packageFingerprintService)
        return target
    }

}
