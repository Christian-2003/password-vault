package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.SourceKeyService
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import javax.inject.Inject


/**
 * Use case to verify whether a master password is valid.
 *
 * @param readonlyAuthRepository    Repository to access auth data.
 * @param sourceKeyService          Service for source key handling.
 */
class VerifyMasterPasswordUseCase @Inject internal constructor(
    private val readonlyAuthRepository: ReadonlyAuthRepository,
    private val sourceKeyService: SourceKeyService
) {

    /**
     * Verifies the validity of the specified master password.
     *
     * @param masterPassword    Master password to verify.
     * @return                  Whether the master password is valid.
     */
    suspend fun verify(masterPassword: CharArray): Boolean {
        if (masterPassword.isEmpty()) {
            return false
        }

        val encryptedKekBytes: ByteArray? = readonlyAuthRepository.getMasterPasswordKek()
        val saltBytes: ByteArray? = readonlyAuthRepository.getMasterPasswordSalt()
        var decryptedKekBytes: ByteArray? = null

        try {
            if (encryptedKekBytes == null || saltBytes == null) {
                return false
            }

            decryptedKekBytes = sourceKeyService.decryptKekWithSource(encryptedKekBytes, masterPassword, saltBytes)

            return true
        }
        catch (_: Exception) {
            return false
        }
        finally {
            encryptedKekBytes?.fill(0)
            saltBytes?.fill(0)
            decryptedKekBytes?.fill(0)
        }
    }

}
