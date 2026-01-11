package de.christian2003.security.application.services

import de.christian2003.security.domain.exceptions.UnlockFailedException
import de.christian2003.security.domain.exceptions.UnlockSourceInvalidException
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.SetupMasterKeyRepository
import de.christian2003.security.domain.repositories.SetupMasterPasswordRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import java.security.InvalidKeyException
import javax.crypto.SecretKey

class UnlockMasterKeyFromPasswordService(
    private val masterPasswordRepository: SetupMasterPasswordRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val masterKeyRepository: SetupMasterKeyRepository,
    private val cipherService: CipherService,
    private val kdfService: KdfService
) {

    fun unlockMasterKey(masterPassword: CharArray): ByteArray {
        if (masterPassword.isEmpty()) {
            throw UnlockSourceInvalidException("Master password cannot be empty to unlock master key")
        }

        //Get password salt:
        val salt: ByteArray? = masterPasswordRepository.getMasterPasswordSalt()
        if (salt == null) {
            throw UnlockFailedException("Cannot unlock master key because master password salt is not available")
        }

        //Derive wrapped key:
        val wrappedKeyBytes: ByteArray = kdfService.derive(masterPassword, salt)

        //Unwrap key:
        val unwrappedKeyBytes: ByteArray = unwrapKey(wrappedKeyBytes)

        //Get KEK:
        val encryptedKekBytes: ByteArray? = masterPasswordRepository.getEncryptedMasterPasswordKek()
        if (encryptedKekBytes == null) {
            throw UnlockFailedException("KEK unavailable for master key")
        }

        //Decrypt KEK:
        val decryptedKekBytes: ByteArray = try {
            cipherService.decrypt(encryptedKekBytes, unwrappedKeyBytes)
        } catch (e: InvalidKeyException) {
            throw UnlockSourceInvalidException("Master password is invalid: ${e.message ?: "Unknown error"}")
        } catch (e: Exception) {
            throw UnlockFailedException("Cannot decrypt KEK from master password: ${e.message ?: "Unknown error"}")
        }

        //Get MK:
        val encryptedMasterKeyBytes: ByteArray? = masterKeyRepository.getEncryptedMasterKey()
        if (encryptedMasterKeyBytes == null) {
            throw UnlockFailedException("Master key does not exist")
        }

        //Decrypt MK:
        val decryptedMasterKeyBytes: ByteArray = try {
            cipherService.decrypt(encryptedMasterKeyBytes, decryptedKekBytes)
        } catch (e: Exception) {
            throw UnlockFailedException("Cannot decrypt MK from master password: ${e.message ?: "Unknown error"}")
        }

        //Wipe internal arrays:
        salt.fill(0)
        wrappedKeyBytes.fill(0)
        unwrappedKeyBytes.fill(0)
        encryptedKekBytes.fill(0)
        decryptedKekBytes.fill(0)
        encryptedMasterKeyBytes.fill(0)

        return decryptedMasterKeyBytes
    }


    private fun unwrapKey(wrappedKeyBytes: ByteArray): ByteArray {
        val hardwareBackedKey: SecretKey? = hardwareBackedKeyRepository.getKey("master_password_key")
        if (hardwareBackedKey == null) {
            throw UnlockFailedException("Cannot unwrap key because hardware-backed key is unavailable")
        }

        val unwrappedKeyBytes: ByteArray = try {
            cipherService.decrypt(wrappedKeyBytes, hardwareBackedKey)
        } catch (e: Exception) {
            throw UnlockFailedException("Cannot unwrap key: ${e.message ?: "Unknown error"}")
        }

        return unwrappedKeyBytes
    }

}
