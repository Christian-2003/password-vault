package de.christian2003.security.application.usecases

import de.christian2003.security.application.services.RecoveryCodeEncoderService
import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.application.usecases.dto.RecoveryCodeGeneratorResultDto
import de.christian2003.security.domain.entities.RecoveryCodes
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.DecryptedKekRepository
import de.christian2003.security.domain.repositories.RecoveryCodesRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
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
 */
class SetupNewRecoveryCodesUseCase @Inject constructor(
    private val recoveryCodesRepository: RecoveryCodesRepository,
    private val kekRepository: DecryptedKekRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val kdfService: KdfService,
    private val cipherService: CipherService,
    private val saltGeneratorService: SaltGeneratorService,
    private val recoveryCodeEncoderService: RecoveryCodeEncoderService
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
                val recoveryCode: RecoveryCodeGeneratorResultDto = generateSingleRecoveryCode(
                    index = index,
                    decryptedKekBytes = decryptedKek,
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
     * @param index                 Index of the recovery code.
     * @param decryptedKekBytes     Bytes of the decrypted kek.
     * @param random                Secure random.
     * @return                      Generated recovery code.
     * @throws AuthSetupException   Cannot generate recovery code.
     */
    private suspend fun generateSingleRecoveryCode(
        index: Int,
        decryptedKekBytes: ByteArray,
        random: SecureRandom
    ): RecoveryCodeGeneratorResultDto = coroutineScope {
        //Recovery codes are encoded with Crockford's Base32: XXXX-XXXX-XXXX-XXXX-XXXX-XXXX
        //6 Segments * 4 Characters * 5 Bits per character = 120 Bits
        val recoveryCodeBytes = ByteArray((6 * 4 * 5) / 8) //Divide by 8 to get bytes
        random.nextBytes(recoveryCodeBytes)

        //Internal buffers:
        var salt: ByteArray? = null
        var sourceKeyBytes: ByteArray? = null
        var encryptedKekBytes: ByteArray? = null

        try {
            //Generate data:
            salt = saltGeneratorService.generateSalt()
            sourceKeyBytes = deriveSourceKey(recoveryCodeBytes, salt)
            encryptedKekBytes = encryptKek(decryptedKekBytes, sourceKeyBytes)

            //Return result:
            val result = RecoveryCodeGeneratorResultDto(
                index = index,
                recoveryCodeBytes = recoveryCodeBytes,
                salt = salt,
                encryptedKek = encryptedKekBytes
            )
            return@coroutineScope result
        }
        catch (e: Exception) {
            sourceKeyBytes?.fill(0)
            encryptedKekBytes?.fill(0)
            throw e
        }
    }


    /**
     * Derives the source key from the specified recovery code and salt.
     *
     * @param recoveryCode          Recovery code from which to derive the source key.
     * @param salt                  Salt to use to derive the source key.
     * @throws AuthSetupException   Cannot derive source key.
     */
    private suspend fun deriveSourceKey(recoveryCode: ByteArray, salt: ByteArray): ByteArray {
        if (recoveryCode.isEmpty()) {
            throw AuthSetupException("Recovery code cannot be empty")
        }

        try {
            val sourceKeyBytes: ByteArray = kdfService.derive(recoveryCode, salt)
            return sourceKeyBytes
        } catch (e: Exception) {
            throw AuthSetupException("Cannot derive source key (${e.message ?: "Unknown error"})")
        }
    }


    /**
     * Encrypts the KEK using the specified source key as well as a hardware-backed key.
     *
     * @param decryptedKekBytes     Bytes of the KEK to encrypt.
     * @param sourceKeyBytes        Bytes of the source key used for encrypting the KEK.
     * @throws AuthSetupException   Cannot encrypt the KEK.
     */
    private suspend fun encryptKek(decryptedKekBytes: ByteArray, sourceKeyBytes: ByteArray): ByteArray {
        //Encrypt KEK using source key:
        val encryptedKekBytes: ByteArray = try {
            cipherService.encrypt(decryptedKekBytes, sourceKeyBytes)
        } catch (e: Exception) {
            throw AuthSetupException("Cannot encrypt KEK using source key (${e.message ?: "Unknown error"})")
        }

        //Get hardware-backed key:
        val hardwareBackedKeyAlias: String = SecurityAliases.HardwareBackedKey.getAlias()
        val hardwareBackedKey: SecretKey? = hardwareBackedKeyRepository.getKey(hardwareBackedKeyAlias)
        if (hardwareBackedKey == null) {
            throw AuthSetupException("Cannot encrypt KEK, because hardware-backed key is unavailable")
        }

        //Encrypt KEK using hardware-backed key:
        try {
            val encryptedKekWithHwBytes: ByteArray = cipherService.encrypt(encryptedKekBytes, hardwareBackedKey)
            return encryptedKekWithHwBytes
        }
        catch (e: Exception) {
            throw AuthSetupException("Cannot encrypt KEK using hardware-backed key (${e.message ?: "Unknown error"})")
        }
        finally {
            encryptedKekBytes.fill(0)
        }
    }

}
