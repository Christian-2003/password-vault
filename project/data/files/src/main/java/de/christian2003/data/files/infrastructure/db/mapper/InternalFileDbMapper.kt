package de.christian2003.data.files.infrastructure.db.mapper

import de.christian2003.core.security.domain.services.HmacCipherService
import de.christian2003.data.files.domain.entities.InternalFile
import de.christian2003.data.files.domain.entities.InternalFileMetadata
import de.christian2003.data.files.infrastructure.db.entities.InternalFileEntity
import javax.inject.Inject


internal class InternalFileDbMapper @Inject constructor(
    private val cipherService: HmacCipherService
) {

    suspend fun toDomain(entity: InternalFileEntity): InternalFile {
        val decryptedName: ByteArray = cipherService.decrypt(entity.encryptedActualFileName, entity.internalName.toByteArray())
        val nameAsString: String = String(decryptedName)

        return InternalFile(
            internalName = entity.internalName,
            actualFileName = nameAsString,
            metadata = InternalFileMetadata(
                createdAt = entity.createdAt,
                editedAt = entity.editedAt,
                accessedAt = entity.accessedAt,
                size = entity.size
            )
        )
    }


    suspend fun toEntity(domain: InternalFile): InternalFileEntity {
        val nameAsBytes: ByteArray = domain.actualFileName.encodeToByteArray()
        val encryptedName: ByteArray = cipherService.encrypt(nameAsBytes, domain.internalName.toByteArray())

        return InternalFileEntity(
            internalName = domain.internalName,
            encryptedActualFileName = encryptedName,
            createdAt = domain.metadata.createdAt,
            editedAt = domain.metadata.editedAt,
            accessedAt = domain.metadata.accessedAt,
            size = domain.metadata.size
        )
    }

}
