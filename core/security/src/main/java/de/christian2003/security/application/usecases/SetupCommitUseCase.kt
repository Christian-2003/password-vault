package de.christian2003.security.application.usecases

import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.repositories.SetupCommitRepository
import de.christian2003.security.domain.repositories.SetupKekRepository
import de.christian2003.security.domain.repositories.SetupMasterKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KeyGeneratorService

class SetupCommitUseCase(
    private val kekRepository: SetupKekRepository,
    private val masterKeyRepository: SetupMasterKeyRepository,
    private val commitRepository: SetupCommitRepository,
    private val cipherService: CipherService,
    private val keyGeneratorService: KeyGeneratorService
) {

    fun commit() {
        val decryptedKek: ByteArray? = kekRepository.getDecryptedKek()
        if (decryptedKek == null) {
            throw AuthSetupException("Cannot commit changes because there is no decrypted KEK available")
        }

        if (!masterKeyRepository.hasEncryptedMasterKey()) {
            //New master key needs to be generated and encrypted:
            val masterKeyBytes: ByteArray = keyGeneratorService.generate()
            val encryptedMasterKeyBytes: ByteArray = try {
                cipherService.encrypt(masterKeyBytes, decryptedKek)
            } catch (e: Exception) {
                throw AuthSetupException("Newly created master key cannot be encrypted using KEK")
            }
            masterKeyRepository.setEncryptedMasterKey(encryptedMasterKeyBytes)
        }

        commitRepository.commitAllChanges()
    }

}
