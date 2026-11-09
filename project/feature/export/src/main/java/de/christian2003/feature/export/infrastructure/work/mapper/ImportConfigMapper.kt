package de.christian2003.feature.export.infrastructure.work.mapper

import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ImportOptions
import de.christian2003.feature.export.infrastructure.work.dto.ImportConfigDto
import javax.inject.Inject


/**
 * Mapper to convert the ImportConfig to / from its domain representation and DTO.
 * The DTO is required for JSON serialization since Android work manager does not accept custom types.
 */
internal class ImportConfigMapper @Inject constructor() {

    /**
     * Converts the provided DTO to its domain representation.
     *
     * @param dto   DTO to convert.
     * @return      Converted domain object.
     */
    fun toDomain(dto: ImportConfigDto): ImportConfig {
        return ImportConfig(
            accountsImportOptions = ImportOptions.entries[dto.accountsImportOptions],
            importFile = dto.importFile,
            decryptionKeySeed = dto.decryptionKeySeed
        )
    }


    /**
     * Converts the provided domain objects to its DTO.
     *
     * @param domain    Domain object to convert.
     * @return          Converted DTO.
     */
    fun toDto(domain: ImportConfig): ImportConfigDto {
        return ImportConfigDto(
            accountsImportOptions = domain.accountsImportOptions.ordinal,
            importFile = domain.importFile,
            decryptionKeySeed = domain.decryptionKeySeed
        )
    }

}
