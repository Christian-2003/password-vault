package de.christian2003.feature.export.infrastructure.work.mapper

import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.infrastructure.work.dto.ExportConfigDto
import javax.inject.Inject


internal class ExportConfigMapper @Inject constructor() {

    fun toDomain(dto: ExportConfigDto): ExportConfig {
        return ExportConfig(
            accounts = dto.accounts,
            files = dto.files,
            exportDestination = dto.exportDestination,
            encryptionKeySeed = dto.encryptionKeySeed
        )
    }


    fun toDto(domain: ExportConfig): ExportConfigDto {
        return ExportConfigDto(
            accounts = domain.accounts,
            files = domain.files,
            exportDestination = domain.exportDestination,
            encryptionKeySeed = domain.encryptionKeySeed
        )
    }

}
