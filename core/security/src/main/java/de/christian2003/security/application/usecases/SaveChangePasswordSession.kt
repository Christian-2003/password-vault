package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.application.services.SourceKeyService
import de.christian2003.security.domain.entities.ChangePasswordSession
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.exceptions.AuthTransactionException
import de.christian2003.security.domain.exceptions.UnlockFailedException
import de.christian2003.security.domain.exceptions.UnlockSourceInvalidException
import de.christian2003.security.domain.repositories.AuthTransactionRepository
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


/**
 * Use case to save the session data for changing the master password.
 *
 * @param authRepository            Repository to set auth data.
 * @param readonlyAuthRepository    Repository to read auth data.
 * @param sourceKeyService          Service for source key handling.
 * @param saltGeneratorService      Service to generate salts.
 */
class SaveChangePasswordSession @Inject internal constructor(
    private val authRepository: AuthTransactionRepository,
    private val readonlyAuthRepository: ReadonlyAuthRepository,
    private val sourceKeyService: SourceKeyService,
    private val saltGeneratorService: SaltGeneratorService
) {

    /**
     * Saves the provided session to permanent storage.
     *
     * @param session   Session data for changing the master password.
     */
    suspend fun save(session: ChangePasswordSession) {
        if (session.currentMasterPassword.isEmpty()) {
            throw AuthSetupException("Current master password cannot be empty")
        }
        if (session.newMasterPassword.isEmpty()) {
            throw AuthSetupException("New master password cannot be empty")
        }

        var decryptedKekBytes: ByteArray? = null

        try {
            authRepository.beginTransaction()

            decryptedKekBytes = getDecryptedKekFromMasterPassword(session.currentMasterPassword)

            saveMasterPassword(session.newMasterPassword, decryptedKekBytes)

            authRepository.commitTransaction()
        }
        finally {
            decryptedKekBytes?.fill(0)
        }
    }


    /**
     * Retrieves the decrypted KEK from the specified master password. If a decrypted KEK cannot be
     * obtained, an exception is thrown.
     *
     * @param masterPassword                Master password from which to obtain the decrypted KEK.
     * @return                              Bytes of the decrypted KEK.
     * @throws UnlockSourceInvalidException The master password is invalid.
     * @throws UnlockFailedException        The KEK cannot be decrypted.
     * @throws AuthSetupException           The master password has not yet been set up.
     */
    private suspend fun getDecryptedKekFromMasterPassword(masterPassword: CharArray): ByteArray {
        val saltBytes: ByteArray? = readonlyAuthRepository.getMasterPasswordSalt()
        val encryptedKekBytes: ByteArray? = readonlyAuthRepository.getMasterPasswordKek()
        var decryptedKekBytes: ByteArray?

        try {
            if (saltBytes == null || encryptedKekBytes == null) {
                throw UnlockFailedException("Master password is not set up")
            }

            //KEK was decrypted successfully:
            decryptedKekBytes = sourceKeyService.decryptKekWithSource(encryptedKekBytes, masterPassword, saltBytes)
            return decryptedKekBytes
        }
        finally {
            saltBytes?.fill(0)
            encryptedKekBytes?.fill(0)
        }
    }


    /**
     * Saves the master password to the repository.
     *
     * @param masterPassword            Master password to save.
     * @param decryptedKekBytes         Decrypted KEK.
     * @throws AuthSetupException       Cannot encrypt KEK.
     * @throws AuthTransactionException Cannot save password to repository.
     */
    private suspend fun saveMasterPassword(masterPassword: CharArray, decryptedKekBytes: ByteArray) = coroutineScope {
        val salt: ByteArray = saltGeneratorService.generateSalt()
        val encryptedKekBytes: ByteArray = sourceKeyService.encryptKekWithSource(masterPassword, salt, decryptedKekBytes, false)

        authRepository.setMasterPassword(encryptedKekBytes, salt)
    }

}
