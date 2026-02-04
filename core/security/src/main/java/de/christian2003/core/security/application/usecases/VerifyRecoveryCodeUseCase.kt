package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.application.services.SourceKeyService
import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


/**
 * Use case to verify the validity of a recovery code that is entered by the user.
 *
 * @param readonlyAuthRepository    Repository for auth data.
 * @param sourceKeyService          Service for source key handling.
 */
class VerifyRecoveryCodeUseCase @Inject internal constructor(
    private val readonlyAuthRepository: ReadonlyAuthRepository,
    private val sourceKeyService: SourceKeyService
) {

    /**
     * Verifies whether the specified recovery code is valid.
     *
     * @param recoveryCode  Recovery code to verify.
     * @return              Whether the recovery code specified is valid.
     */
    suspend fun verify(recoveryCode: CharArray): Boolean = coroutineScope {
        val numberOfRecoveryCodes: Int = readonlyAuthRepository.getNumberOfRecoveryCodes()
        val deferredResults: List<Deferred<Boolean>> = (0 until numberOfRecoveryCodes).map { index ->
            async {
                val success: Boolean = verifyRecoveryCode(recoveryCode, index)
                return@async success
            }
        }

        //Wait for all deferred results:
        val results: List<Boolean> = deferredResults.awaitAll()

        val success: Boolean = results.firstOrNull { it } ?: false

        return@coroutineScope success
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
    private suspend fun verifyRecoveryCode(recoveryCode: CharArray, index: Int): Boolean = coroutineScope {
        val saltBytes: ByteArray? = readonlyAuthRepository.getRecoveryCodeSalt(index)
        val encryptedKeyBytes: ByteArray? = readonlyAuthRepository.getRecoveryCodeKek(index)
        var decryptedKekBytes: ByteArray? = null

        //Decrypt recovery code:
        try {
            if (saltBytes == null || encryptedKeyBytes == null) {
                return@coroutineScope false
            }

            decryptedKekBytes = sourceKeyService.decryptKekWithSource(encryptedKeyBytes, recoveryCode, saltBytes)

            //KEK was decrypted successfully:
            return@coroutineScope true
        }
        catch (_: Exception) {
            return@coroutineScope false
        }
        finally {
            saltBytes?.fill(0)
            encryptedKeyBytes?.fill(0)
            decryptedKekBytes?.fill(0)
        }
    }

}
