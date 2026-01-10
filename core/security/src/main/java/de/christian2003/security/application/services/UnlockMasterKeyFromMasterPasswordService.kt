package de.christian2003.security.application.services

import android.security.keystore.KeyProperties
import de.christian2003.security.domain.entities.KekEntry
import de.christian2003.security.domain.exceptions.InvalidKeyException
import de.christian2003.security.domain.exceptions.MasterKeyUnlockFailedException
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.KekRepository
import de.christian2003.security.domain.repositories.MasterKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Service used to unlock the master key through the master password.
 *
 * @param hardwareBackedKeyRepository   Repository to access a hardware-backed key.
 * @param kekRepository                 Repository to access the KEK.
 * @param masterKeyRepository           Repository to access the master key.
 * @param cipherService                 Service used to perform encryption operations.
 * @param kdfService                    Service used to perform KDF operations.
 */
class UnlockMasterKeyFromMasterPasswordService @Inject constructor(
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val kekRepository: KekRepository,
    private val masterKeyRepository: MasterKeyRepository,
    private val cipherService: CipherService,
    private val kdfService: KdfService
) {

    /**
     * Unlocks the master key from the provided master password. If the master key cannot be unlocked,
     * an exception is thrown.
     *
     * @param masterPassword                    Master password from which to unlock the master key.
     * @return                                  Bytes of the master key.
     * @throws MasterKeyUnlockFailedException   Master key cannot be unlocked.
     */
    fun unlockMasterKey(masterPassword: CharArray): ByteArray {
        //Derive wrapped password key:
        val wrappedPasswordKey: ByteArray = kdfService.derive(masterPassword)

        //Unwrap password key with hardware-backed key:
        val hardwareBackedKey: SecretKey = if (hardwareBackedKeyRepository.containsKey("master_password_key")) {
            hardwareBackedKeyRepository.getKey("master_password_key") ?: throw IllegalStateException("Hardware-backed key should exist, but cannot be retrieved")
        } else {
            hardwareBackedKeyRepository.generateNewKey(
                alias = "master_password_key",
                algorithm = KeyProperties.KEY_ALGORITHM_HMAC_SHA512,
                purposes = KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
        }
        val unwrappedPasswordKey: ByteArray = try {
            cipherService.decrypt(wrappedPasswordKey, hardwareBackedKey)
        } catch (e: InvalidKeyException) {
            throw MasterKeyUnlockFailedException(e.message ?: "")
        }

        //Decrypt KEK:
        val encryptedKek: ByteArray? = kekRepository.getEncryptedKek(KekEntry.MasterPassword)
        if (encryptedKek == null) {
            throw MasterKeyUnlockFailedException("KEK is not available")
        }
        val decryptedKek: ByteArray = try {
            cipherService.decrypt(encryptedKek, unwrappedPasswordKey)
        } catch (e: InvalidKeyException) {
            throw MasterKeyUnlockFailedException(e.message ?: "")
        }

        //Decrypt MK:
        val encryptedMasterKey: ByteArray? = masterKeyRepository.getEncryptedMasterKey()
        if (encryptedMasterKey == null) {
            throw MasterKeyUnlockFailedException("Master key is not available")
        }
        val decryptedMasterKey: ByteArray = try {
            cipherService.decrypt(encryptedMasterKey, decryptedKek)
        } catch (e: InvalidKeyException) {
            throw MasterKeyUnlockFailedException("Master key cannot be decrypted")
        }

        //Wipe internal keys:
        wrappedPasswordKey.fill(0)
        unwrappedPasswordKey.fill(0)
        encryptedKek.fill(0)
        decryptedKek.fill(0)
        encryptedMasterKey.fill(0)

        //Return master key:
        return decryptedMasterKey
    }

}
