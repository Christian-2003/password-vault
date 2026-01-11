package de.christian2003.security.application.services

import de.christian2003.security.domain.entities.KekEntry
import de.christian2003.security.domain.entities.MasterKeyUnlockMethod
import de.christian2003.security.domain.exceptions.UnlockSourceInvalidException
import de.christian2003.security.domain.exceptions.UnlockFailedException
import de.christian2003.security.domain.exceptions.UnlockMethodNotSetupException
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.KekRepository
import de.christian2003.security.domain.repositories.MasterKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import java.security.InvalidKeyException
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
class UnlockMasterKeyFromSourceService @Inject constructor(
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val kekRepository: KekRepository,
    private val masterKeyRepository: MasterKeyRepository,
    private val cipherService: CipherService,
    private val kdfService: KdfService
) {

    /**
     * Unlocks the master key from the provided input source (e.g. master password or recovery codes).
     * If the master key cannot be unlocked, an exception is thrown.
     *
     * @param sourceValue                       Source value from which to unlock the master key
     *                                          (e.g. master password or recovery codes).
     * @param unlockMethod                      Method for unlocking the master key.
     * @return                                  Bytes of the master key.
     * @throws UnlockSourceInvalidException     The provided source value is invalid.
     * @throws UnlockMethodNotSetupException    The invoked unlock method is not setup.
     * @throws UnlockFailedException            Master key cannot be unlocked for some other reason.
     */
    fun unlockMasterKey(
        sourceValue: CharArray,
        unlockMethod: MasterKeyUnlockMethod
    ): ByteArray {
        if (sourceValue.isEmpty()) {
            throw UnlockSourceInvalidException("Unlock source for $unlockMethod cannot be empty")
        }

        //Derive wrapped source key:
        val wrappedSourceKey: ByteArray = kdfService.derive(sourceValue)

        //Retrieve hardware-backed key for unwrapping source key:
        val hardwareBackedKeyAlias: String = getHardwareBackedKeyAlias(unlockMethod)
        val hardwareBackedKey: SecretKey? = hardwareBackedKeyRepository.getKey(hardwareBackedKeyAlias)
        if (hardwareBackedKey == null) {
            throw UnlockMethodNotSetupException("Hardware-backed not available for unlock method $unlockMethod")
        }

        //Unwrap source key with hardware-backed key:
        val unwrappedSourceKey: ByteArray = try {
            cipherService.decrypt(wrappedSourceKey, hardwareBackedKey)
        } catch (e: InvalidKeyException) {
            throw UnlockSourceInvalidException("Source key for $unlockMethod cannot be unwrapped: ${e.message ?: "Unknown error"}")
        } catch (e: Exception) {
            throw UnlockFailedException("An error occurred when unwrapping source key for $unlockMethod: ${e.message ?: "Unknown error"}")
        }

        //Decrypt KEK:
        val kekEntry: KekEntry = when(unlockMethod) {
            MasterKeyUnlockMethod.MasterPassword -> KekEntry.MasterPassword
            MasterKeyUnlockMethod.RecoveryCodes -> KekEntry.RecoveryCodes
        }
        val encryptedKek: ByteArray? = kekRepository.getEncryptedKek(kekEntry)
        if (encryptedKek == null) {
            throw UnlockMethodNotSetupException("KEK for $unlockMethod is not available")
        }
        val decryptedKek: ByteArray = try {
            cipherService.decrypt(encryptedKek, unwrappedSourceKey)
        } catch (e: InvalidKeyException) {
            throw UnlockSourceInvalidException("KEK for $unlockMethod cannot be decrypted: ${e.message ?: "Unknown error"}")
        } catch (e: Exception) {
            throw UnlockFailedException("An error occurred when decrypting KEK for $unlockMethod: ${e.message ?: "Unknown error"}")
        }

        //Decrypt MK:
        val encryptedMasterKey: ByteArray? = masterKeyRepository.getEncryptedMasterKey()
        if (encryptedMasterKey == null) {
            throw UnlockMethodNotSetupException("Master key is not available")
        }
        val decryptedMasterKey: ByteArray = try {
            cipherService.decrypt(encryptedMasterKey, decryptedKek)
        } catch (e: InvalidKeyException) {
            throw UnlockSourceInvalidException("MK for $unlockMethod cannot be decrypted using KEK: ${e.message ?: "Unknown error"}")
        } catch (e: Exception) {
            throw UnlockFailedException("An error occurred when decrypting MK for $unlockMethod: ${e.message ?: "Unknown error"}")
        }

        //Wipe internal keys:
        wrappedSourceKey.fill(0)
        unwrappedSourceKey.fill(0)
        encryptedKek.fill(0)
        decryptedKek.fill(0)
        encryptedMasterKey.fill(0)

        //Return master key:
        return decryptedMasterKey
    }


    /**
     * Gets the alias for the hardware-backed key based on the specified MK unlock source.
     *
     * @param unlockMethod  Source for the master key unlock.
     * @return              Alias for the hardware-backed key.
     */
    private fun getHardwareBackedKeyAlias(unlockMethod: MasterKeyUnlockMethod): String {
        return when (unlockMethod) {
            MasterKeyUnlockMethod.MasterPassword -> "master_password_key"
            MasterKeyUnlockMethod.RecoveryCodes -> "recovery_codes_key"
        }
    }

}
