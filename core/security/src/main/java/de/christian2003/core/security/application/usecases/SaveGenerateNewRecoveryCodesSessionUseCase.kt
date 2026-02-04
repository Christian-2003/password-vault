package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.application.services.SaltGeneratorService
import de.christian2003.core.security.application.services.SourceKeyService
import de.christian2003.core.security.domain.entities.GenerateNewRecoveryCodesSession
import de.christian2003.core.security.domain.exceptions.AuthSetupException
import de.christian2003.core.security.domain.exceptions.AuthTransactionException
import de.christian2003.core.security.domain.exceptions.UnlockFailedException
import de.christian2003.core.security.domain.exceptions.UnlockSourceInvalidException
import de.christian2003.core.security.domain.repositories.AuthTransactionRepository
import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


/**
 * Use case to save the session for the generation of new recovery codes.
 *
 * @param authRepository            Repository to set the auth data.
 * @param readonlyAuthRepository    Repository to read auth data.
 * @param sourceKeyService          Service for source key handling.
 * @param saltGeneratorService      Service to generate salts.
 */
class SaveGenerateNewRecoveryCodesSessionUseCase @Inject internal constructor(
    private val authRepository: AuthTransactionRepository,
    private val readonlyAuthRepository: ReadonlyAuthRepository,
    private val sourceKeyService: SourceKeyService,
    private val saltGeneratorService: SaltGeneratorService
) {

    /**
     * Saves the provided session to permanent storage.
     *
     * @param session   Session data for generating new recovery codes.
     */
    suspend fun save(session: GenerateNewRecoveryCodesSession) = coroutineScope {
        if (session.masterPassword.isEmpty()) {
            throw AuthSetupException("Master password cannot be empty")
        }
        if (session.recoveryCodes.isEmpty()) {
            throw AuthSetupException("Recovery codes cannot be empty")
        }
        else {
            session.recoveryCodes.forEach { recoveryCode ->
                if (recoveryCode.isEmpty()) {
                    throw AuthSetupException("Recovery code cannot be empty")
                }
            }
        }

        var decryptedKekBytes: ByteArray? = null

        try {
            authRepository.beginTransaction()

            decryptedKekBytes = getDecryptedKekFromMasterPassword(session.masterPassword)

            //Save recovery codes:
            val deferred: List<Deferred<Unit>> = session.recoveryCodes.map { recoveryCode ->
                async {
                    saveNewRecoveryCode(recoveryCode, decryptedKekBytes)
                }
            }
            deferred.awaitAll()

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
     * Saves the specified recovery code to the repository.
     *
     * @param recoveryCode              Recovery code to save.
     * @param decryptedKekBytes         Decrypted KEK.
     * @throws AuthSetupException       Cannot encrypt KEK.
     * @throws AuthTransactionException Cannot save code to repository.
     */
    private suspend fun saveNewRecoveryCode(recoveryCode: CharArray, decryptedKekBytes: ByteArray) = coroutineScope {
        val salt: ByteArray = saltGeneratorService.generateSalt()
        val encryptedKekBytes: ByteArray = sourceKeyService.encryptKekWithSource(recoveryCode, salt, decryptedKekBytes, false)

        authRepository.addRecoveryCode(encryptedKekBytes, salt)
    }

}
