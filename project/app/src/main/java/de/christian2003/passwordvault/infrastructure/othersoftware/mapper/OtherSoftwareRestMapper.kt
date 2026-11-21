package de.christian2003.passwordvault.infrastructure.othersoftware.mapper

import de.christian2003.passwordvault.domain.entities.OtherSoftware
import de.christian2003.passwordvault.infrastructure.othersoftware.dto.SoftwareItemDto
import javax.inject.Inject


/**
 * Mapper which maps the REST DTO for other software to advertise to its domain representation.
 */
internal class OtherSoftwareRestMapper @Inject constructor() {

    /**
     * Maps the REST DTO to its domain representation.
     *
     * @param dto   DTO to map to the domain.
     * @return      Mapped domain representation.
     */
    fun toDomain(dto: SoftwareItemDto): OtherSoftware {
        val domain = OtherSoftware(
            packageName = dto.packageName,
            websiteUrl = dto.websiteUrl,
            softwareName = dto.softwareName,
            iconUrl = dto.iconUrl
        )

        return domain
    }

}
