package de.christian2003.passwordvault.plugin.infrastructure.db.mapper

import de.christian2003.passwordvault.domain.entry.Entry
import de.christian2003.passwordvault.domain.security.CipherService
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.EntryEntity
import kotlinx.serialization.cbor.Cbor


/**
 * Mapper maps the domain model 'Entry' to the database entity.
 */
class EntryDbMapper(

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
     * Maps the database entity that is passed as argument to the domain model 'Entry'.
     *
     * @param entity    Database entity to map to the domain model 'Entry'.
     * @return          Domain model 'Entry'.
     */
    fun toDomain(entity: EntryEntity): Entry {
        val decryptedPayload: ByteArray = cipherService.decrypt(entity.payload, entity.id.toByteArray())
        val payload: EntryPayload = cbor.decodeFromByteArray(EntryPayload.serializer(), decryptedPayload)

        return Entry(
            id = entity.id,
            name = payload.name,
            description = payload.description,
            created = payload.created,
            edited = payload.edited
        )
    }


    /**
     * Maps the domain model 'Entry' that is passed as argument to the database entity.
     *
     * @param domain    Domain model 'Entry' ti map to the database entity.
     * @return          Database entity.
     */
    fun toEntity(domain: Entry): EntryEntity {
        val payload = EntryPayload(
            name = domain.name,
            description = domain.description,
            created = domain.created,
            edited = domain.edited
        )

        val serializedPayload: ByteArray = cbor.encodeToByteArray(EntryPayload.serializer(), payload)
        val encryptedPayload: ByteArray = cipherService.encrypt(serializedPayload, domain.id.toByteArray())

        return EntryEntity(
            id = domain.id,
            payload = encryptedPayload
        )
    }

}
