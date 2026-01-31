package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.application.services.SourceKeyService
import de.christian2003.security.domain.entities.RecoverySession
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.exceptions.AuthTransactionException
import de.christian2003.security.domain.exceptions.UnlockSourceInvalidException
import de.christian2003.security.domain.repositories.AuthTransactionRepository
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


/**
 * Use case to save the session data for the recovery of the master password.
 *
 * @param authRepository            Repository to set the authentication data.
 * @param readonlyAuthRepository    Repository to read auth data.
 * @param saltGeneratorService      Service to generate salts.
 * @param sourceKeyService          Service for source key handling.
 */
class SaveRecoverySessionUseCase @Inject constructor(
    private val authRepository: AuthTransactionRepository,
    private val readonlyAuthRepository: ReadonlyAuthRepository,
    private val saltGeneratorService: SaltGeneratorService,
    private val sourceKeyService: SourceKeyService
) {

    /**
     * Saves the provided session to permanent storage.
     *
     * @param session   Session data for the recovery.
     */
    suspend fun save(session: RecoverySession) = coroutineScope {
        if (session.recoveryCode.isEmpty()) {
            throw AuthSetupException("Recovery code cannot be empty")
        }
        if (session.newMasterPassword.isEmpty()) {
            throw AuthSetupException("New master password cannot be empty")
        }
        if (session.newRecoveryCodes.isEmpty()) {
            throw AuthSetupException("New recovery codes cannot be empty")
        }
        else {
            session.newRecoveryCodes.forEach { recoveryCode ->
                if (recoveryCode.isEmpty()) {
                    throw AuthSetupException("New recovery code cannot be empty")
                }
            }
        }

        var decryptedKekBytes: ByteArray? = null

        try {
            authRepository.beginTransaction()

            //Get decrypted KEK:
            decryptedKekBytes = getDecryptedKekFromCurrentRecoveryCode(session.recoveryCode)
            if (decryptedKekBytes == null) {
                throw UnlockSourceInvalidException("Recovery code invalid")
            }

            //Start async operations:
            val deferredMasterPassword: Deferred<Unit> = async {
                saveMasterPassword(session.newMasterPassword, decryptedKekBytes)
            }
            val deferredRecoveryCodes: List<Deferred<Unit>> = session.newRecoveryCodes.map { recoveryCode ->
                async {
                    saveNewRecoveryCode(recoveryCode, decryptedKekBytes)
                }
            }

            //Wait for async operations:
            awaitAll(deferredMasterPassword, *deferredRecoveryCodes.toTypedArray())

            authRepository.commitTransaction()
        }
        catch (e: Exception) {
            throw e
        }
        finally {
            decryptedKekBytes?.fill(0)
        }
    }


    /**
     * Returns the decrypted KEK from the specified recovery code. If the decrypted KEK cannot be
     * obtained, null is returned.
     *
     * @param recoveryCode  Recovery code from which to decrypt the KEK.
     * @return              Decrypted KEK or null.
     */
    private suspend fun getDecryptedKekFromCurrentRecoveryCode(recoveryCode: CharArray): ByteArray? = coroutineScope {
        val numberOfRecoveryCodes: Int = readonlyAuthRepository.getNumberOfRecoveryCodes()
        val deferredResults: List<Deferred<ByteArray?>> = (0 until numberOfRecoveryCodes).map { index ->
            async {
                val decryptedKekBytes: ByteArray? = decryptKekWithCurrentRecoveryCode(recoveryCode, index)
                return@async decryptedKekBytes
            }
        }

        //Wait for deferred results:
        val results: List<ByteArray?> = deferredResults.awaitAll()

        val decryptedKek: ByteArray? = results.firstOrNull { it != null }
        return@coroutineScope decryptedKek
    }


    /**
     * Verifies the specified recovery code against the stored recovery code with the provided index.
     *
     * @param recoveryCode  Recovery code to verify.
     * @param index         Index of the stored recovery code against which to verify the passed
     *                      code.
     * @return              Whether the passed recovery code matches the code stored with the
     *                      specified index.
     */
    private suspend fun decryptKekWithCurrentRecoveryCode(recoveryCode: CharArray, index: Int): ByteArray? = coroutineScope {
        val saltBytes: ByteArray? = readonlyAuthRepository.getRecoveryCodeSalt(index)
        val encryptedKeyBytes: ByteArray? = readonlyAuthRepository.getRecoveryCodeKek(index)
        var decryptedKekBytes: ByteArray?

        //Decrypt recovery code:
        try {
            if (saltBytes == null || encryptedKeyBytes == null) {
                return@coroutineScope null
            }

            decryptedKekBytes = sourceKeyService.decryptKekWithSource(
                encryptedKeyBytes,
                recoveryCode,
                saltBytes
            )

            //KEK was decrypted successfully:
            return@coroutineScope decryptedKekBytes
        }
        catch (_: Exception) {
            return@coroutineScope null
        }
        finally {
            saltBytes?.fill(0)
            encryptedKeyBytes?.fill(0)
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
