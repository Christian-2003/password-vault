package de.christian2003.passwordvault.plugin.infrastructure.db.mapper

import de.christian2003.passwordvault.domain.entry.Detail
import de.christian2003.passwordvault.domain.security.CipherService
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.DetailEntity
import kotlinx.serialization.cbor.Cbor


/**
 * Mapper maps the domain model 'Detail' to the database entity.
 */
class DetailDbMapper(

    /**
     * Cipher service to use for encryption and decryption.
     */
    private val cipherService: CipherService,

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
    fun toDomain(entity: DetailEntity): Detail {
        val decryptedPayload: ByteArray = cipherService.decrypt(entity.payload, entity.entry.toByteArray())
        val payload: DetailPayload = cbor.decodeFromByteArray(DetailPayload.serializer(), decryptedPayload)

        return Detail(
            id = entity.id,
            entry = entity.entry,
            name = payload.name,
            content = payload.content,
            type = payload.type,
            icon = payload.icon,
            isObfuscated = payload.isObfuscated,
            isVisible = payload.isVisible,
            created = payload.created,
            edited = payload.edited
        )
    }


    /**
     * Maps the domain model 'Detail' that is passed as argument to the database entity.
     *
     * @param domain    Domain model 'Detail' ti map to the database entity.
     * @return          Database entity.
     */
    fun toEntity(domain: Detail): DetailEntity {
        val payload = DetailPayload(
            name = domain.name,
            content = domain.content,
            type = domain.type,
            icon = domain.icon,
            isObfuscated = domain.isObfuscated,
            isVisible = domain.isVisible,
            created = domain.created,
            edited = domain.edited
        )

        val serializedPayload: ByteArray = cbor.encodeToByteArray(DetailPayload.serializer(), payload)
        val encryptedPayload: ByteArray = cipherService.encrypt(serializedPayload, domain.entry.toByteArray())

        return DetailEntity(
            id = domain.id,
            entry = domain.entry,
            payload = encryptedPayload
        )
    }

}
