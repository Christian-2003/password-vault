package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.SourceKeyService
import de.christian2003.security.domain.exceptions.UnlockFailedException
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import de.christian2003.security.domain.repositories.UnlockedMasterKeyRepository
import de.christian2003.security.domain.services.CipherService
import javax.inject.Inject


/**
 * Use case to unlock the master key using the master password.
 *
 * @param readonlyAuthRepository        Repository to access auth data.
 * @param unlockedMasterKeyRepository   Repository to access the unlocked master key.
 * @param cipherService                 Service to perform cryptographic operations.
 * @param sourceKeyService              Service for source key handling.
 */
class UnlockWithMasterPasswordUseCase @Inject internal constructor(
    private val readonlyAuthRepository: ReadonlyAuthRepository,
    private val unlockedMasterKeyRepository: UnlockedMasterKeyRepository,
    private val cipherService: CipherService,
    private val sourceKeyService: SourceKeyService
) {

    /**
     * Unlocks the master key with the provided master password.
     *
     * @param masterPassword            Master password to use for unlocking.
     * @return                          Whether the master key was unlocked successfully.
     * @throws UnlockFailedException    The master key cannot be unlocked (e.g. because the setup
     *                                  has not been completed)
     */
    suspend fun unlock(masterPassword: CharArray): Boolean {
        if (masterPassword.isEmpty()) {
            return false
        }

        val encryptedKekBytes: ByteArray? = readonlyAuthRepository.getMasterPasswordKek()
        val saltBytes: ByteArray? = readonlyAuthRepository.getMasterPasswordSalt()
        val encryptedMasterKeyBytes: ByteArray? = readonlyAuthRepository.getEncryptedMasterKey()
        var decryptedKekBytes: ByteArray? = null
        var decryptedMasterKeyBytes: ByteArray?

        try {
            if (encryptedKekBytes == null || saltBytes == null || encryptedMasterKeyBytes == null) {
                throw UnlockFailedException("Master password has not yet been set up")
            }

            decryptedKekBytes = try {
                sourceKeyService.decryptKekWithSource(encryptedKekBytes, masterPassword, saltBytes)
            } catch (_: Exception) {
                //Master password is invalid:
                return false
            }

            decryptedMasterKeyBytes = try {
                cipherService.decrypt(encryptedMasterKeyBytes, decryptedKekBytes)
            } catch (_: Exception) {
                return false
            }

            unlockedMasterKeyRepository.setUnlockedMasterKeyBytes(decryptedMasterKeyBytes)
            return true
        }
        finally {
            encryptedKekBytes?.fill(0)
            saltBytes?.fill(0)
            encryptedMasterKeyBytes?.fill(0)
            decryptedKekBytes?.fill(0)
        }
    }

}
