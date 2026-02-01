package de.christian2003.security.application.services

import android.security.keystore.KeyProperties
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.exceptions.UnlockFailedException
import de.christian2003.security.domain.exceptions.UnlockSourceInvalidException
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import de.christian2003.security.domain.services.KeyGeneratorService
import kotlinx.coroutines.coroutineScope
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Service provides methods that can be used in conjunction with source keys (e.g. master passwords
 * or recovery codes).
 *
 * @param hardwareBackedKeyRepository   Repository for hardware-backed keys.
 * @param cipherService                 Service for cryptographic operations.
 * @param kdfService                    Service for key derivation.
 * @param keyGeneratorService           Service for key generation.
 */
internal class SourceKeyService @Inject constructor(
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val cipherService: CipherService,
    private val kdfService: KdfService,
    private val keyGeneratorService: KeyGeneratorService
) {

    /**
     * Encrypts the KEK using the provided source (i.e. master password or recovery code) and salt.
     *
     * @param source                        Source used for encryption (i.e. master password or
     *                                      recovery code).
     * @param salt                          Salt used for KDF.
     * @param generateNewHwKeyIfRequired    Whether to generate a new hardware-backed key if none is
     *                                      available. This is usually only required during setup.
     * @return                              Encrypted KEK.
     * @throws AuthSetupException           Cannot encrypt the KEK.
     */
    suspend fun encryptKekWithSource(source: CharArray, salt: ByteArray, decryptedKekBytes: ByteArray, generateNewHwKeyIfRequired: Boolean = false) = coroutineScope {
        val hardwareBackedKey: SecretKey? = getOrGenerateHardwareBackedKey(generateNewHwKeyIfRequired)
        var sourceKeyBytes: ByteArray? = null
        var partlyEncryptedKekBytes: ByteArray? = null
        var fullyEncryptedKekBytes: ByteArray? = null

        try {
            if (hardwareBackedKey == null) {
                throw AuthSetupException("Hardware-backed key is unavailable")
            }

            sourceKeyBytes = try {
                kdfService.derive(source, salt)
            } catch (e: Exception) {
                throw AuthSetupException("Cannot derive source key (${e.message ?: "Unknown error"})")
            }

            partlyEncryptedKekBytes = try {
                cipherService.encrypt(decryptedKekBytes, sourceKeyBytes)
            } catch (e: Exception) {
                throw AuthSetupException("Cannot encrypt KEK using source key (${e.message ?: "Unknown error"})")
            }

            fullyEncryptedKekBytes = try {
                cipherService.encrypt(partlyEncryptedKekBytes, hardwareBackedKey)
            } catch (e: Exception) {
                throw AuthSetupException("Cannot encrypt KEK using hardware-backed key (${e.message ?: "Unknown error"})")
            }

            return@coroutineScope fullyEncryptedKekBytes
        }
        finally {
            sourceKeyBytes?.fill(0)
            partlyEncryptedKekBytes?.fill(0)
        }
    }


    /**
     * Decrypts the specified KEK with the provided source and salt.
     *
     * @param encryptedKekBytes             Bytes of the encrypted KEK to decrypt.
     * @param source                        Source used for decrypting the KEK.
     * @param salt                          Salt used to derive a key from the specified source.
     * @return                              Bytes of the decrypted KEK.
     * @throws UnlockSourceInvalidException The provided source is invalid and was not used to encrypt
     *                                      the specified KEK beforehand.
     * @throws UnlockFailedException        Cannot decrypt KEK.
     */
    suspend fun decryptKekWithSource(encryptedKekBytes: ByteArray, source: CharArray, salt: ByteArray): ByteArray {
        val hardwareBackedKey: SecretKey? = getOrGenerateHardwareBackedKey(false)
        var sourceKeyBytes: ByteArray? = null
        var partlyDecryptedKekBytes: ByteArray? = null
        var fullyDecryptedKekBytes: ByteArray? = null

        //Decrypt recovery code:
        try {
            if (hardwareBackedKey == null) {
                throw UnlockFailedException("Hardware-backed key unavailable")
            }

            sourceKeyBytes = try {
                kdfService.derive(source, salt)
            } catch (e: Exception) {
                throw UnlockSourceInvalidException("Cannot derive key from source (${e.message ?: "Unknown error"})")
            }

            partlyDecryptedKekBytes = try {
                cipherService.decrypt(encryptedKekBytes, hardwareBackedKey)
            } catch (e: Exception) {
                throw UnlockSourceInvalidException("Cannot decrypt KEK with hardware-backed key (${e.message ?: "Unknown error"})")
            }

            fullyDecryptedKekBytes = try {
                cipherService.decrypt(partlyDecryptedKekBytes, sourceKeyBytes)
            } catch (e: Exception) {
                throw UnlockSourceInvalidException("Cannot decrypt KEK with source key (${e.message ?: "Unknown error"})")
            }

            //KEK was decrypted successfully:
            return fullyDecryptedKekBytes
        }
        finally {
            sourceKeyBytes?.fill(0)
            partlyDecryptedKekBytes?.fill(0)
        }
    }


    /**
     * Gets the hardware-backed key or generates a new one if none is available.
     *
     * @return  Hardware-backed key.
     */
    private fun getOrGenerateHardwareBackedKey(generateNewHwKeyIfRequired: Boolean): SecretKey? {
        val alias: String = SecurityAliases.HardwareBackedKey.getAlias()
        if (hardwareBackedKeyRepository.containsKey(alias)) {
            val key: SecretKey? = hardwareBackedKeyRepository.getKey(alias)
            if (key != null) {
                return key
            }
        }

        if (generateNewHwKeyIfRequired) {
            val keyGenParameterSpec = keyGeneratorService.getKeyGenParameterSpec(alias)
            val key: SecretKey = hardwareBackedKeyRepository.generateNewKey(
                alias = alias,
                algorithm = KeyProperties.KEY_ALGORITHM_AES,
                keyGenParameterSpec = keyGenParameterSpec
            )
            return key
        }

        return null
    }

}
