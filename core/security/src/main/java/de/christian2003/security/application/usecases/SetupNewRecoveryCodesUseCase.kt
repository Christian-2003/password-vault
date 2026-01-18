package de.christian2003.security.application.usecases

import android.security.keystore.KeyProperties
import de.christian2003.security.application.services.RecoveryCodeEncoderService
import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.application.usecases.dto.RecoveryCodeGeneratorResultDto
import de.christian2003.security.domain.entities.RecoveryCodes
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.DecryptedKekRepository
import de.christian2003.security.domain.repositories.RecoveryCodesRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import de.christian2003.security.domain.services.KeyGeneratorService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * use case for the authentication setup of the recovery codes. This use case generates the recovery
 * codes and stores them in the repository. The codes are only stored in permanent memory when they
 * are committed later.
 * The codes should be shown to the user before committing.
 *
 * @param recoveryCodesRepository       Repository to access encrypted KEK from the master password.
 * @param kekRepository                 Repository to access the current decrypted KEK.
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 * @param kdfService                    Service for KDF operations.
 * @param cipherService                 Service for cryptographic operations.
 * @param saltGeneratorService          Service to generate random salt.
 * @param recoveryCodeEncoderService    Service to encode recovery codes.
 * @param keyGeneratorService           Service to generate cryptographic keys.
 */
class SetupNewRecoveryCodesUseCase @Inject constructor(
    private val recoveryCodesRepository: RecoveryCodesRepository,
    private val kekRepository: DecryptedKekRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val kdfService: KdfService,
    private val cipherService: CipherService,
    private val saltGeneratorService: SaltGeneratorService,
    private val recoveryCodeEncoderService: RecoveryCodeEncoderService,
    private val keyGeneratorService: KeyGeneratorService
) {

    /**
     * Number of recovery codes to generate when calling this service.
     */
    private val numberOfRecoveryCodes: Int = 5


    /**
     * Sets the recovery keys.
     *
     * @return  Recovery keys that were generated.
     */
    suspend fun setupRecoveryCodes(): RecoveryCodes {
        val recoveryCodesAsBytes: List<ByteArray> = generateRecoveryCodes()

        //Transform codes to char arrays:
        val recoveryCodesAsCharArrays: MutableList<CharArray> = mutableListOf()
        recoveryCodesAsBytes.forEach { recoveryCodeBytes ->
            val encoded: CharArray = recoveryCodeEncoderService.encode(recoveryCodeBytes)
            recoveryCodesAsCharArrays.add(encoded)
        }

        val result = RecoveryCodes(
            codes = recoveryCodesAsCharArrays
        )
        return result
    }


    /**
     * Generates a list of recovery codes.
     *
     * @return  Recovery codes.
     */
    private suspend fun generateRecoveryCodes(): List<ByteArray> = coroutineScope {
        val random = SecureRandom()
        val recoveryCodes: MutableList<ByteArray> = mutableListOf()

        val decryptedKek: ByteArray? = kekRepository.getDecryptedKek()
        if (decryptedKek == null) {
            throw AuthSetupException("Cannot generate recovery codes because there is no KEK available")
        }

        //Asynchronously generate all recovery codes:
        val deferredResults: List<Deferred<RecoveryCodeGeneratorResultDto>> = (0 until numberOfRecoveryCodes).map { index ->
            async(Dispatchers.Default) {
                val recoveryCode: RecoveryCodeGeneratorResultDto =
                    generateSingleRecoveryCode(
                    index = index,
                    decryptedKek = decryptedKek,
                    random = random
                )
                return@async recoveryCode
            }
        }

        //Wait for recovery codes to finish generating:
        val recoveryCodeDtos: List<RecoveryCodeGeneratorResultDto> = deferredResults.awaitAll()

        //Save generated codes:
        recoveryCodeDtos.forEach { recoveryCode ->
            recoveryCodesRepository.setEncryptedRecoveryKek(
                index = recoveryCode.index,
                encryptedKekBytes = recoveryCode.encryptedKek,
                salt = recoveryCode.salt
            )

            recoveryCodes.add(recoveryCode.recoveryCodeBytes)
        }

        return@coroutineScope recoveryCodes
    }


    /**
     * Generates a single recovery code.
     *
     * @param index         Index of the recovery code.
     * @param decryptedKek  Bytes of the decrypted kek.
     * @param random        Secure random.
     * @return              Generated recovery code.
     */
    private suspend fun generateSingleRecoveryCode(
        index: Int,
        decryptedKek: ByteArray,
        random: SecureRandom
    ): RecoveryCodeGeneratorResultDto = coroutineScope {
        //Recovery codes are encoded with Crockford's Base32: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX
        //6 Segments * 4 Characters * 5 Bits per character = 120 Bits
        val recoveryCodeBytes = ByteArray((6 * 4 * 5) / 8) //Divide by 8 to get bytes
        random.nextBytes(recoveryCodeBytes)

        //Generate salt:
        val salt: ByteArray = saltGeneratorService.generateSalt() //Always generate new salt for new code

        //Derive key for encryption:
        val unwrappedKeyBytes: ByteArray = kdfService.derive(recoveryCodeBytes, salt)

        //Wrap key:
        val wrappedKeyBytes: ByteArray = wrapSourceKey(unwrappedKeyBytes)

        val encryptedKek: ByteArray = try {
            cipherService.encrypt(decryptedKek, wrappedKeyBytes)
        } catch (e: Exception) {
            throw AuthSetupException("Cannot encrypt KEK for recovery code $index: ${e.message ?: "Unknown error"}")
        }

        val result = RecoveryCodeGeneratorResultDto(
            index = index,
            recoveryCodeBytes = recoveryCodeBytes,
            salt = salt,
            encryptedKek = encryptedKek
        )

        unwrappedKeyBytes.fill(0)
        wrappedKeyBytes.fill(0)

        return@coroutineScope result
    }


    /**
     * Wraps the source key.
     *
     * @param unwrappedKeyBytes Bytes of the source key to wrap.
     * @return                  Bytes of the wrapped source key.
     */
    private suspend fun wrapSourceKey(unwrappedKeyBytes: ByteArray): ByteArray {
        val hardwareBackedKeyAlias = "recovery_codes_key"

        var hardwareBackedKey: SecretKey? = hardwareBackedKeyRepository.getKey(hardwareBackedKeyAlias)
        if (hardwareBackedKey == null) {
            hardwareBackedKey = hardwareBackedKeyRepository.generateNewKey(
                alias = hardwareBackedKeyAlias,
                algorithm = KeyProperties.KEY_ALGORITHM_AES,
                keyGenParameterSpec = keyGeneratorService.getKeyGenParameterSpec(hardwareBackedKeyAlias)
            )
        }

        try {
            val keyBytes: ByteArray = cipherService.encrypt(unwrappedKeyBytes, hardwareBackedKey)
            val trimmedKeyBytes: ByteArray = keyBytes.take(32).toByteArray()
            keyBytes.fill(0)
            return trimmedKeyBytes
        }
        catch (e: Exception) {
            throw AuthSetupException("Source key cannot be wrapped for master password: ${e.message ?: "Unknown error"}")
        }
    }

}
