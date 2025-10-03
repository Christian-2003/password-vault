package de.christian2003.passwordvault.plugin.infrastructure.db.mapper

import de.christian2003.passwordvault.domain.model.account.Account
import de.christian2003.passwordvault.domain.model.account.AccountDescriptor
import de.christian2003.passwordvault.domain.model.account.AccountMetadata
import de.christian2003.passwordvault.domain.security.CipherService
import de.christian2003.passwordvault.plugin.infrastructure.db.dto.AccountPayload
import de.christian2003.passwordvault.plugin.infrastructure.db.entities.AccountEntity
import kotlinx.serialization.cbor.Cbor


/**
 * Mapper maps the domain model 'Account' to the database entity.
 */
class AccountDbMapper(

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
     * Maps the database entity that is passed as argument to the domain model 'Account'.
     *
     * @param entity    Database entity to map to the domain model 'Account'.
     * @return          Domain model 'Account'.
     */
    fun toDomain(entity: AccountEntity): Account {
        val decryptedPayload: ByteArray = cipherService.decrypt(entity.payload, entity.id.toByteArray())
        val payload: AccountPayload = cbor.decodeFromByteArray(AccountPayload.serializer(), decryptedPayload)

        return Account(
            descriptor = AccountDescriptor(
                id = entity.id,
                name = payload.name,
                description = payload.description
            ),
            metadata = AccountMetadata(
                createdAt = entity.createdAt,
                editedAt = entity.editedAt,
                accessedAt = entity.accessedAt
            )
        )
    }


    /**
     * Maps the domain model 'Account' that is passed as argument to the database entity.
     *
     * @param domain    Domain model 'Account' ti map to the database entity.
     * @return          Database entity.
     */
    fun toEntity(domain: Account): AccountEntity {
        val payload = AccountPayload(
            name = domain.descriptor.name,
            description = domain.descriptor.description
        )

        val serializedPayload: ByteArray = cbor.encodeToByteArray(AccountPayload.serializer(), payload)
        val encryptedPayload: ByteArray = cipherService.encrypt(serializedPayload, domain.descriptor.id.toByteArray())

        return AccountEntity(
            id = domain.descriptor.id,
            payload = encryptedPayload,
            createdAt = domain.metadata.createdAt,
            editedAt = domain.metadata.editedAt,
            accessedAt = domain.metadata.accessedAt
        )
    }

}
