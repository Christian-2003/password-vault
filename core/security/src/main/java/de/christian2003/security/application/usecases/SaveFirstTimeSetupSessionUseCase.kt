package de.christian2003.security.application.usecases

import android.security.keystore.KeyProperties
import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.domain.entities.FirstTimeSetupSession
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.exceptions.AuthTransactionException
import de.christian2003.security.domain.repositories.AuthTransactionRepository
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KdfService
import de.christian2003.security.domain.services.KeyGeneratorService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Use case to save the session data for the first-time app setup.
 *
 * @param authRepository                Repository to set the authentication data.
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 * @param kdfService                    Service for KDF.
 * @param cipherService                 Service for cryptographic operations.
 * @param keyGeneratorService           Service to generate keys.
 * @param saltGeneratorService          Service to generate salts.
 */
class SaveFirstTimeSetupSessionUseCase @Inject constructor(
    private val authRepository: AuthTransactionRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val kdfService: KdfService,
    private val cipherService: CipherService,
    private val keyGeneratorService: KeyGeneratorService,
    private val saltGeneratorService: SaltGeneratorService
) {

    /**
     * Saves the provided session to permanent storage.
     *
     * @param session   Session data for the first-time app setup.
     */
    suspend fun save(session: FirstTimeSetupSession) = coroutineScope {
        if (session.masterPassword == null || session.masterPassword.isEmpty()) {
            throw AuthSetupException("Master password cannot be empty")
        }
        if (session.recoveryCodes == null) {
            throw AuthSetupException("No recovery codes provided")
        }
        else {
            session.recoveryCodes.forEach { recoveryCode ->
                if (recoveryCode.isEmpty()) {
                    throw AuthSetupException("Recovery code cannot be empty")
                }
            }
        }

        try {
            authRepository.beginTransaction()

            val decryptedKekBytes: ByteArray = keyGeneratorService.generate()

            //Start async operations:
            val deferredMasterPassword: Deferred<Unit> = async {
                saveMasterPassword(session.masterPassword, decryptedKekBytes)
            }
            val deferredRecoveryCodes: List<Deferred<Unit>> = session.recoveryCodes.map { recoveryCode ->
                async {
                    saveRecoveryCode(recoveryCode, decryptedKekBytes)
                }
            }
            val deferredMasterKey: Deferred<Unit> = async {
                saveMasterKey(decryptedKekBytes)
            }

            //TODO: Biometrics setup!

            //Await all async operations:
            awaitAll(deferredMasterPassword, *deferredRecoveryCodes.toTypedArray(), deferredMasterKey)

            authRepository.commitTransaction()
        }
        catch (e: Exception) {
            throw e
        }
    }


    /**
     * Saves the master password to the repository.
     *
     * @param masterPassword            Master password to save.
     * @param decryptedKekBytes         Decrypted KEK.
     * @throws AuthSetupException       Cannot encrypt KEK.
     * @throws AuthTransactionException Cannot save password to repository.
     */
    private suspend fun saveMasterPassword(masterPassword: CharArray, decryptedKekBytes: ByteArray) = coroutineScope {
        val salt: ByteArray = saltGeneratorService.generateSalt()
        val encryptedKekBytes: ByteArray = encryptKekWithSource(masterPassword, salt, decryptedKekBytes)

        authRepository.setMasterPassword(encryptedKekBytes, salt)
    }


    /**
     * Saves the specified recovery code to the repository.
     *
     * @param recoveryCode              Recovery code to save.
     * @param decryptedKekBytes         Decrypted KEK.
     * @throws AuthSetupException       Cannot encrypt KEK.
     * @throws AuthTransactionException Cannot save code to repository.
     */
    private suspend fun saveRecoveryCode(recoveryCode: CharArray, decryptedKekBytes: ByteArray) = coroutineScope {
        val salt: ByteArray = saltGeneratorService.generateSalt()
        val encryptedKekBytes: ByteArray = encryptKekWithSource(recoveryCode, salt, decryptedKekBytes)

        authRepository.addRecoveryCode(encryptedKekBytes, salt)
    }


    /**
     * Generates a master key which is encrypted using the provided KEK and saved to the repository.
     *
     * @param decryptedKekBytes         KEK used to encrypt the master key.
     * @throws AuthSetupException       Cannot encrypt the master key.
     * @throws AuthTransactionException Cannot save the key to the repository.
     */
    private suspend fun saveMasterKey(decryptedKekBytes: ByteArray) = coroutineScope {
        val masterKey: ByteArray = keyGeneratorService.generate()
        val encryptedKeyBytes: ByteArray = try {
            cipherService.encrypt(masterKey, decryptedKekBytes)
        } catch (e: Exception) {
            throw AuthSetupException("Cannot encrypt master key using KEK (${e.message ?: "Unknown error"})")
        }

        authRepository.setMasterKey(encryptedKeyBytes)
    }


    /**
     * Encrypts the KEK using the provided source (i.e. master password or recovery code) and salt.
     *
     * @param source                Source used for encryption (i.e. master password or recovery code).
     * @param salt                  Salt used for KDF.
     * @return                      Encrypted KEK.
     * @throws AuthSetupException   Cannot encrypt the KEK.
     */
    private suspend fun encryptKekWithSource(source: CharArray, salt: ByteArray, decryptedKekBytes: ByteArray) = coroutineScope {
        val hardwareBackedKey: SecretKey = getOrGenerateHardwareBackedKey()
        var sourceKeyBytes: ByteArray? = null
        var partlyEncryptedKekBytes: ByteArray? = null
        var fullyEncryptedKekBytes: ByteArray? = null

        try {
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
     * Gets the hardware-backed key or generates a new one if none is available.
     *
     * @return  Hardware-backed key.
     */
    private fun getOrGenerateHardwareBackedKey(): SecretKey {
        val alias: String = SecurityAliases.HardwareBackedKey.getAlias()
        if (hardwareBackedKeyRepository.containsKey(alias)) {
            val key: SecretKey? = hardwareBackedKeyRepository.getKey(alias)
            if (key != null) {
                return key
            }
        }

        val keyGenParameterSpec = keyGeneratorService.getKeyGenParameterSpec(alias)
        val key: SecretKey = hardwareBackedKeyRepository.generateNewKey(
            alias = alias,
            algorithm = KeyProperties.KEY_ALGORITHM_AES,
            keyGenParameterSpec = keyGenParameterSpec
        )
        return key
    }

}
