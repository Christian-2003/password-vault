package de.christian2003.security.application.usecases

import android.security.keystore.KeyProperties
import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.DecryptedKekRepository
import de.christian2003.security.domain.repositories.MasterPasswordRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import de.christian2003.security.domain.services.KeyGeneratorService
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Use case for the authentication setup of the master password. Whenever a new master password needs
 * to be set, this use case needs to be called.
 *
 * @param masterPasswordRepository      Repository to access encrypted KEK from the master password.
 * @param kekRepository                 Repository to access the current decrypted KEK.
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 * @param kdfService                    Service for KDF operations.
 * @param cipherService                 Service for cryptographic operations.
 * @param keyGeneratorService           Service to generate cryptographic keys.
 * @param saltGeneratorService          Service to generate random salt.
 */
class SetupNewMasterPasswordUseCase @Inject constructor(
    private val masterPasswordRepository: MasterPasswordRepository,
    private val kekRepository: DecryptedKekRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val kdfService: KdfService,
    private val cipherService: CipherService,
    private val keyGeneratorService: KeyGeneratorService,
    private val saltGeneratorService: SaltGeneratorService
) {

    /**
     * Sets the master password. All data generated in this step is not stored in permanent memory
     * until committed later.
     *
     * @param masterPassword    Master password to set.
     */
    suspend fun setupMasterPassword(masterPassword: CharArray) {
        if (masterPassword.isEmpty()) {
            throw AuthSetupException("Master password cannot be empty")
        }

        val sourceKeySalt: ByteArray = saltGeneratorService.generateSalt() //Always generate new salt for password setup!

        val unwrappedSourceKeyBytes: ByteArray = try {
            kdfService.derive(masterPassword, sourceKeySalt)
        } catch (e: Exception) {
            throw AuthSetupException("Key cannot be derived from master password: ${e.message ?: "Unknown error"}")
        }

        val wrappedSourceKeyBytes: ByteArray = wrapSourceKey(unwrappedSourceKeyBytes)

        val decryptedKekBytes: ByteArray = getDecryptedKek()

        val encryptedKekBytes: ByteArray = try {
            cipherService.encrypt(decryptedKekBytes, wrappedSourceKeyBytes)
        } catch (e: Exception) {
            throw AuthSetupException("KEK cannot be encrypted using master password: ${e.message ?: "Unknown error"}")
        }

        masterPasswordRepository.setEncryptedMasterPasswordKek(encryptedKekBytes, sourceKeySalt)
    }


    /**
     * Wraps the source key.
     *
     * @param unwrappedKeyBytes Bytes of the source key to wrap.
     * @return                  Bytes of the wrapped source key.
     */
    private suspend fun wrapSourceKey(unwrappedKeyBytes: ByteArray): ByteArray {
        val hardwareBackedKeyAlias = "master_password_key"

        var hardwareBackedKey: SecretKey? = hardwareBackedKeyRepository.getKey(hardwareBackedKeyAlias)
        if (hardwareBackedKey == null) {
            hardwareBackedKey = hardwareBackedKeyRepository.generateNewKey(
                alias = hardwareBackedKeyAlias,
                algorithm = KeyProperties.KEY_ALGORITHM_AES,
                purposes = KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
        }

        try {
            return cipherService.encrypt(unwrappedKeyBytes, hardwareBackedKey)
        }
        catch (e: Exception) {
            throw AuthSetupException("Source key cannot be wrapped for master password: ${e.message ?: "Unknown error"}")
        }
    }


    /**
     * Returns the decrypted KEK.
     *
     * @return  Bytes of the decrypted KEK.
     */
    private suspend fun getDecryptedKek(): ByteArray {
        var decryptedKek: ByteArray? = kekRepository.getDecryptedKek()
        if (decryptedKek == null) {
            decryptedKek = keyGeneratorService.generate()
            kekRepository.setDecryptedKek(decryptedKek)
        }

        return decryptedKek
    }

}
