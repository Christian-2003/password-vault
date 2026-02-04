package de.christian2003.passwordvault.plugin.infrastructure.db.mapper

import de.christian2003.passwordvault.domain.model.detail.Detail
import de.christian2003.passwordvault.domain.model.detail.DetailMetadata
import de.christian2003.passwordvault.plugin.infrastructure.db.dto.DetailPayload
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.DetailEntity
import de.christian2003.core.security.domain.services.HmacCipherService
import kotlinx.serialization.cbor.Cbor
import kotlin.uuid.Uuid


/**
 * Mapper maps the domain model 'Detail' to the database entity.
 */
class DetailDbMapper(

    /**
     * Cipher service to use for encryption and decryption.
     */
    private val cipherService: HmacCipherService,

    /**
     * Cbor to use for serialization.
     */
    private val cbor: Cbor

) {

    /**
     * Maps the database entity that is passed as argument to the domain model 'Detail'.
     *
     * @param entity    Database entity to map to the domain model 'Detail'.
     * @return          Domain model 'Detail'.
     */
    suspend fun toDomain(entity: DetailEntity): Detail {
        val decryptedPayload: ByteArray = cipherService.decrypt(entity.payload, entity.account.toByteArray())
        val payload: DetailPayload = cbor.decodeFromByteArray(DetailPayload.serializer(), decryptedPayload)

        return Detail(
            id = entity.id,
            name = payload.name,
            content = payload.content,
            type = payload.type,
            icon = payload.icon,
            metadata = DetailMetadata(
                createdAt = entity.createdAt,
                editedAt = entity.editedAt,
                isObfuscated = entity.isObfuscated,
                isVisible = entity.isVisible,
            )
        )
    }


    /**
     * Maps the domain model 'Detail' that is passed as argument to the database entity.
     *
     * @param domain    Domain model 'Detail' ti map to the database entity.
     * @param entry     ID of the entry to which the detail is assigned.
     * @return          Database entity.
     */
    suspend fun toEntity(domain: Detail, entry: Uuid): DetailEntity {
        val payload = DetailPayload(
            name = domain.name,
            content = domain.content,
            type = domain.type,
            icon = domain.icon
        )

        val serializedPayload: ByteArray = cbor.encodeToByteArray(DetailPayload.serializer(), payload)
        val encryptedPayload: ByteArray = cipherService.encrypt(serializedPayload, entry.toByteArray())

        return DetailEntity(
            id = domain.id,
            account = entry,
            payload = encryptedPayload,
            createdAt = domain.metadata.createdAt,
            editedAt = domain.metadata.editedAt,
            isObfuscated = domain.metadata.isObfuscated,
            isVisible = domain.metadata.isVisible
        )
    }

}
