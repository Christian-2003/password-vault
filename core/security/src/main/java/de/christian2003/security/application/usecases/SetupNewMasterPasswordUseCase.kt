package de.christian2003.security.application.usecases

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.domain.entities.SecurityAliases
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
        //Internal buffers:
        var salt: ByteArray? = null
        var sourceKeyBytes: ByteArray? = null
        var decryptedKekBytes: ByteArray? = null
        var encryptedKekBytes: ByteArray? = null

        try {
            //Generate data:
            salt = saltGeneratorService.generateSalt()
            sourceKeyBytes = deriveSourceKey(masterPassword, salt)
            decryptedKekBytes = getOrCreateDecryptedKek()
            encryptedKekBytes = encryptKek(decryptedKekBytes, sourceKeyBytes)

            //Store data in repositories:
            masterPasswordRepository.setEncryptedMasterPasswordKek(encryptedKekBytes, salt)
            kekRepository.setDecryptedKek(decryptedKekBytes)
        }
        catch (e: Exception) {
            sourceKeyBytes?.fill(0)
            decryptedKekBytes?.fill(0)
            encryptedKekBytes?.fill(0)
            throw e
        }
    }


    /**
     * Derives the source key from the specified master password and salt.
     *
     * @param masterPassword        Master password from which to derive the source key.
     * @param salt                  Salt to use to derive the source key.
     * @throws AuthSetupException   Cannot derive source key.
     */
    private suspend fun deriveSourceKey(masterPassword: CharArray, salt: ByteArray): ByteArray {
        if (masterPassword.isEmpty()) {
            throw AuthSetupException("Master password cannot be empty")
        }

        try {
            val sourceKeyBytes: ByteArray = kdfService.derive(masterPassword, salt)
            return sourceKeyBytes
        } catch (e: Exception) {
            throw AuthSetupException("Cannot derive source key (${e.message ?: "Unknown error"})")
        }
    }


    /**
     * Gets the decrypted KEK. If no decrypted KEK is available, a new KEK is generated.
     *
     * @return  Bytes of the decrypted KEK.
     */
    private suspend fun getOrCreateDecryptedKek(): ByteArray {
        if (kekRepository.hasDecryptedKek()) {
            //Return existing KEK:
            val decryptedKekBytes: ByteArray? = kekRepository.getDecryptedKek()
            if (decryptedKekBytes != null) {
                return decryptedKekBytes
            }
        }

        //Generate new KEK:
        val decryptedKekBytes: ByteArray = keyGeneratorService.generate()
        return decryptedKekBytes
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
        val hardwareBackedKey: SecretKey = if (hardwareBackedKeyRepository.containsKey(hardwareBackedKeyAlias)) {
            //Return existing key:
            hardwareBackedKeyRepository.getKey(hardwareBackedKeyAlias)!!
        } else {
            //Generate new key:
            val param: KeyGenParameterSpec = keyGeneratorService.getKeyGenParameterSpec(hardwareBackedKeyAlias)
            hardwareBackedKeyRepository.generateNewKey(
                alias = hardwareBackedKeyAlias,
                algorithm = KeyProperties.KEY_ALGORITHM_AES,
                keyGenParameterSpec = param
            )
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
