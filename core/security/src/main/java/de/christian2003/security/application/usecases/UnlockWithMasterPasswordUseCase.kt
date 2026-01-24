package de.christian2003.security.application.usecases

import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.exceptions.UnlockFailedException
import de.christian2003.security.domain.exceptions.UnlockSourceInvalidException
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.MasterKeyRepository
import de.christian2003.security.domain.repositories.MasterPasswordRepository
import de.christian2003.security.domain.repositories.UnlockedMasterKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Use case to unlock the master key using the master password.
 *
 * @param masterKeyRepository           Repository to access the master key.
 * @param masterPasswordRepository      Repository to access the master password.
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 * @param unlockedMasterKeyRepository   Repository to access the unlocked master key.
 * @param kdfService                    Service to perform key derivation.
 * @param cipherService                 Service to perform cryptographic operations.
 */
class UnlockWithMasterPasswordUseCase @Inject constructor(
    private val masterKeyRepository: MasterKeyRepository,
    private val masterPasswordRepository: MasterPasswordRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val unlockedMasterKeyRepository: UnlockedMasterKeyRepository,
    private val kdfService: KdfService,
    private val cipherService: CipherService
) {

    /**
     * Unlocks the master key with the provided master password.
     *
     * @param masterPassword            Master password to use for unlocking.
     * @return                          Whether the master key was unlocked successfully.
     * @throws UnlockFailedException    The master key cannot be unlocked (e.g. because the setup
     *                                  has not been completed)
     */
    suspend fun unlock(masterPassword: CharArray): Boolean {
        if (masterPassword.isEmpty()) {
            return false
        }

        //Buffers:
        var encryptedKekBytes: ByteArray? = null
        var sourceKeyBytes: ByteArray? = null
        var masterKeyBytes: ByteArray? = null

        try {
            encryptedKekBytes = getEncryptedKek()
            sourceKeyBytes = deriveSourceKey(masterPassword)
            masterKeyBytes = unlockMasterKey(sourceKeyBytes, encryptedKekBytes)

            unlockedMasterKeyRepository.setUnlockedMasterKeyBytes(masterKeyBytes)

            return true
        }
        catch (_: UnlockSourceInvalidException) {
            //Master password invalid:
            return false
        }
        catch (e: UnlockFailedException) {
            //Other issues during unlocking:
            throw e
        }
        finally {
            encryptedKekBytes?.fill(0)
            sourceKeyBytes?.fill(0)
            masterKeyBytes?.fill(0)
        }
    }


    /**
     * Returns the KEK that is encrypted with the key that is derived from the master password.
     *
     * @return                          Bytes of the KEK encrypted with the key derived from the
     *                                  master password.
     * @throws UnlockFailedException    The encrypted KEK cannot be retrieved.
     */
    private suspend fun getEncryptedKek(): ByteArray {
        //Get hardware-backed key:
        val hardwareBackedKey: SecretKey? = hardwareBackedKeyRepository.getKey(SecurityAliases.HardwareBackedKey.getAlias())
        if (hardwareBackedKey == null) {
            throw UnlockFailedException("Cannot unlock master key because hardware-backed key is unavailable")
        }

        //Get encrypted KEK:
        val encryptedKekBytes: ByteArray? = masterPasswordRepository.getEncryptedMasterPasswordKek()
        if (encryptedKekBytes == null) {
            throw UnlockFailedException("Cannot unlock master key because KEK is unavailable")
        }

        //Decrypt KEK:
        try {
            val kekBytes: ByteArray = cipherService.decrypt(encryptedKekBytes, hardwareBackedKey)
            return kekBytes
        }
        catch (e: Exception) {
            throw UnlockFailedException("Cannot decrypt KEK (${e.message ?: "Unknown error"})")
        }
        finally {
            encryptedKekBytes.fill(0)
        }
    }


    /**
     * Derives the source key from the specified master password.
     *
     * @param masterPassword            Master password from which to derive the source key.
     * @return                          Bytes of the source key.
     * @throws UnlockFailedException    Cannot derive source key.
     */
    private suspend fun deriveSourceKey(masterPassword: CharArray): ByteArray {
        //Get salt:
        val salt: ByteArray? = masterPasswordRepository.getMasterPasswordSalt()
        if (salt == null) {
            throw UnlockFailedException("Cannot derive source key because salt is unavailable")
        }

        //Derive source key:
        try {
            val sourceKeyBytes = kdfService.derive(masterPassword, salt)
            return sourceKeyBytes
        }
        catch (e: Exception) {
            throw UnlockFailedException("Cannot derive source key (${e.message ?: "Unknown error"})")
        }
        finally {
            salt.fill(0)
        }
    }


    /**
     * Returns the decrypted master key from the source key and the specified encrypted KEK.
     *
     * @param sourceKeyBytes                Bytes of the source key derived from the master password.
     * @param encryptedKek                  Bytes of the encrypted KEK.
     * @return                              Bytes of the decrypted master key.
     * @throws UnlockSourceInvalidException The source key is invalid (i.e. the master password is wrong).
     * @throws UnlockFailedException        Decrypted MK cannot be retrieved.
     */
    private suspend fun unlockMasterKey(sourceKeyBytes: ByteArray, encryptedKek: ByteArray): ByteArray {
        //Decrypt KEK:
        val decryptedKekBytes: ByteArray = try {
            cipherService.decrypt(encryptedKek, sourceKeyBytes)
        } catch (_: Exception) {
            //AES tags do not match (the master password is invalid):
            throw UnlockSourceInvalidException("Invalid master password")
        }

        //Get encrypted MK:
        val encryptedMkBytes: ByteArray? = masterKeyRepository.getEncryptedMasterKey()
        if (encryptedMkBytes == null) {
            decryptedKekBytes.fill(0)
            throw UnlockFailedException("Master key unavailable")
        }

        //Decrypt MK:
        try {
            val mkBytes: ByteArray = cipherService.decrypt(encryptedMkBytes, decryptedKekBytes)
            return mkBytes
        }
        catch (e: Exception) {
            throw UnlockFailedException("Master key cannot be decrypted (${e.message ?: "Unknown error"})")
        }
        finally {
            decryptedKekBytes.fill(0)
            encryptedMkBytes.fill(0)
        }
    }

}
