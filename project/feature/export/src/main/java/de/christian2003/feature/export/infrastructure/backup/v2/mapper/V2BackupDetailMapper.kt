package de.christian2003.feature.export.infrastructure.backup.v2.mapper

import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailMetadata
import de.christian2003.data.accounts.domain.entities.DetailType
import de.christian2003.feature.export.infrastructure.backup.v2.dto.V2BackupDetailDto
import javax.inject.Inject


/**
 * Mapper to map a DTO detail to its domain representation.
 * Other mapping direction (domain -> DTO) is not required since V2 only supports import.
 */
internal class V2BackupDetailMapper @Inject constructor() {

    /**
     * Converts the specified DTO to its domain instance.
     *
     * @param dto   DTO to convert.
     * @return      Converted domain instance.
     */
    fun toDomain(dto: V2BackupDetailDto): Detail {
        var type: DetailType = typeIdToType(dto.typeId)

        //'isUsername' and 'isPassword' no longer exist in v3.8.0! Autofill behavior is determined
        //based on the type instead. So a limited "intelligent" conversion may be appropriate:
        if (type == DetailType.Text && dto.isUsername) {
            type = DetailType.Username
        }
        if (type == DetailType.Number && dto.isPassword) {
            type = DetailType.Pin
        }

        val domain = Detail(
            name = dto.name,
            content = dto.content,
            id = dto.id,
            type = type,
            metadata = DetailMetadata(
                createdAt = dto.created,
                editedAt = dto.edited,
                isObfuscated = dto.isObfuscated,
                isVisible = dto.isVisible
            )
        )

        return domain
    }


    /**
     * Maps the detail type ID from the V2 backup to the domain DetailType.
     *
     * Original mapping (prior to v3.8.0) can be found in
     * 'app\src\main\java\de\passwordvault\model\detail\DetailType.java'
     *
     * @param typeId    Type ID from the V2 backup.
     * @return          Detail type.
     */
    private fun typeIdToType(typeId: Int): DetailType {
        val type: DetailType = when (typeId) {
            0 -> DetailType.Text
            1 -> DetailType.Number
            2 -> DetailType.SecurityQuestion
            3 -> DetailType.Address
            4 -> DetailType.Date
            5 -> DetailType.Email
            6 -> DetailType.Password
            7 -> DetailType.Url
            8 -> DetailType.Pin
            else -> DetailType.Text //Generic fallback
        }

        return type
    }

}
