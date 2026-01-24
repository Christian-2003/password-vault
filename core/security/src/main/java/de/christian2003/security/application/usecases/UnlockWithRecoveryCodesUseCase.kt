package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.RecoveryCodeEncoderService
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.repositories.DecryptedKekRepository
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.RecoveryCodesRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Use case to unlock the KEK using recovery codes.
 *
 * @param recoveryCodesRepository       Repository to access stored recovery code KEKs and salts.
 * @param decryptedKekRepository        Repository in which to store the decrypted KEK after successful
 *                                      unlocking.
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 * @param kdfService                    Service for key derivation.
 * @param cipherService                 Service for cryptographic operations.
 * @param recoveryCodeEncoderService    Service to encode and decode recovery codes.
 */
class UnlockWithRecoveryCodesUseCase @Inject constructor(
    private val recoveryCodesRepository: RecoveryCodesRepository,
    private val decryptedKekRepository: DecryptedKekRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val kdfService: KdfService,
    private val cipherService: CipherService,
    private val recoveryCodeEncoderService: RecoveryCodeEncoderService
) {

    /**
     * Number of recovery codes to generate when calling this service.
     */
    private val numberOfRecoveryCodes: Int = 5


    /**
     * Tries to unlock the KEK (not the MK) with the specified recovery code. If the KEK can be
     * unlocked, the method returns true, otherwise false is returned.
     *
     * @param recoveryCode  Recovery code used to unlock the KEK.
     */
    suspend fun unlock(recoveryCode: CharArray): Boolean = coroutineScope {
        val deferredResults: List<Deferred<ByteArray?>> = (0 until numberOfRecoveryCodes).map { index ->
            async(Dispatchers.Default) {
                val decryptedKek: ByteArray? = verifyRecoveryCode(recoveryCode, index)
                return@async decryptedKek
            }
        }

        //Wait for all recovery codes to be verified:
        val decryptedKeks: List<ByteArray?> = deferredResults.awaitAll()

        val decryptedKek: ByteArray? = decryptedKeks.firstOrNull { it != null }

        if (decryptedKek != null) {
            decryptedKekRepository.setDecryptedKek(decryptedKek)
            return@coroutineScope true
        }
        return@coroutineScope false
    }


    /**
     * Verifies a single recovery code. If the code is valid, the decrypted KEK is returned, otherwise
     * null will be returned.
     *
     * @param recoveryCode  Recovery code to verify.
     * @param index         Index of the stored recovery KEK against to verify the specified code.
     * @return              Decrypted KEK or null if the code is invalid.
     */
    private suspend fun verifyRecoveryCode(recoveryCode: CharArray, index: Int): ByteArray? = coroutineScope {
        //Get KEK and salt:
        val encryptedKekBytes: ByteArray? = recoveryCodesRepository.getEncryptedRecoveryKek(index)
        val saltBytes: ByteArray? = recoveryCodesRepository.getRecoverySalt(index)
        if (encryptedKekBytes == null || saltBytes == null) {
            return@coroutineScope null
        }

        //Decode recovery code:
        val decodedRecoveryCode: ByteArray = try {
            recoveryCodeEncoderService.decode(recoveryCode)
        } catch (_: Exception) {
            //Invalid Base32 char:
            return@coroutineScope null
        }

        //Derive key:
        val sourceKeyBytes: ByteArray = try {
            kdfService.derive(decodedRecoveryCode, saltBytes)
        } catch (_: Exception) {
            return@coroutineScope null
        }

        //Get hardware-backed key:
        val hardwareBackedKey: SecretKey? = hardwareBackedKeyRepository.getKey(SecurityAliases.HardwareBackedKey.getAlias())
        if (hardwareBackedKey == null) {
            return@coroutineScope null
        }

        //Decrypt KEK with hardware-backed key:
        val partlyDecryptedKekBytes: ByteArray = try {
            cipherService.decrypt(encryptedKekBytes, hardwareBackedKey)
        } catch (_: Exception) {
            return@coroutineScope null
        }

        //Decrypt KEK with source key:
        val decryptedKek: ByteArray = try {
            cipherService.decrypt(partlyDecryptedKekBytes, sourceKeyBytes)
        } catch (_: Exception) {
            return@coroutineScope null
        }

        //Wipe internal buffers:
        sourceKeyBytes.fill(0)
        partlyDecryptedKekBytes.fill(0)

        return@coroutineScope decryptedKek
    }

}
