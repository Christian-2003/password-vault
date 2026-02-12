package de.christian2003.data.accounts.infrastructure.db.mapper

import de.christian2003.core.security.domain.services.HmacCipherService
import de.christian2003.data.accounts.domain.entities.Detail
import de.christian2003.data.accounts.domain.entities.DetailMetadata
import de.christian2003.data.accounts.infrastructure.db.dto.DetailPayload
import de.christian2003.data.accounts.infrastructure.db.entities.DetailEntity
import kotlinx.serialization.cbor.Cbor
import kotlin.uuid.Uuid


/**
 * Mapper maps the domain model 'Detail' to the database entity.
 */
internal class DetailDbMapper(

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
            type = entity.type,
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
            icon = domain.icon
        )

        val serializedPayload: ByteArray = cbor.encodeToByteArray(DetailPayload.serializer(), payload)
        val encryptedPayload: ByteArray = cipherService.encrypt(serializedPayload, entry.toByteArray())

        return DetailEntity(
            id = domain.id,
            account = entry,
            payload = encryptedPayload,
            type = domain.type,
            createdAt = domain.metadata.createdAt,
            editedAt = domain.metadata.editedAt,
            isObfuscated = domain.metadata.isObfuscated,
            isVisible = domain.metadata.isVisible
        )
    }

}
