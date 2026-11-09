package de.christian2003.feature.export.infrastructure.work.mapper

import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.infrastructure.work.dto.ExportConfigDto
import javax.inject.Inject


/**
 * Mapper to convert the ExportConfig to / from its domain representation and DTO.
 * The DTO is required for JSON serialization since Android work manager does not accept custom types.
 */
internal class ExportConfigMapper @Inject constructor() {

    /**
     * Converts the provided DTO to its domain representation.
     *
     * @param dto   DTO to convert.
     * @return      Converted domain object.
     */
    fun toDomain(dto: ExportConfigDto): ExportConfig {
        return ExportConfig(
            accounts = dto.accounts,
            files = dto.files,
            exportDestination = dto.exportDestination,
            encryptionKeySeed = dto.encryptionKeySeed
        )
    }


    /**
     * Converts the provided domain objects to its DTO.
     *
     * @param domain    Domain object to convert.
     * @return          Converted DTO.
     */
    fun toDto(domain: ExportConfig): ExportConfigDto {
        return ExportConfigDto(
            accounts = domain.accounts,
            files = domain.files,
            exportDestination = domain.exportDestination,
            encryptionKeySeed = domain.encryptionKeySeed
        )
    }

}
