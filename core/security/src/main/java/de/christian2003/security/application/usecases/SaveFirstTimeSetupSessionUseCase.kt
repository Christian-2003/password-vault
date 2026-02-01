package de.christian2003.security.application.usecases

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import de.christian2003.security.application.services.SaltGeneratorService
import de.christian2003.security.application.services.SourceKeyService
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
 * @param sourceKeyService              Service for source key handling.
 */
class SaveFirstTimeSetupSessionUseCase @Inject internal constructor(
    private val authRepository: AuthTransactionRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val kdfService: KdfService,
    private val cipherService: CipherService,
    private val keyGeneratorService: KeyGeneratorService,
    private val saltGeneratorService: SaltGeneratorService,
    private val sourceKeyService: SourceKeyService
) {

    /**
     * Saves the provided session to permanent storage.
     *
     * @param session   Session data for the first-time app setup.
     */
    suspend fun save(session: FirstTimeSetupSession) = coroutineScope {
        if (session.masterPassword.isEmpty()) {
            throw AuthSetupException("Master password cannot be empty")
        }
        if (session.recoveryCodes.isEmpty()) {
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

            //Synchronously save master password: When saving the master password, some setup happens
            //that is required for further setup steps. Unfortunately, this impacts performance
            //massively.
            //TODO: Defer this as well
            saveMasterPassword(session.masterPassword, decryptedKekBytes)

            //Start async operations:
            val deferredRecoveryCodes: List<Deferred<Unit>> = session.recoveryCodes.map { recoveryCode ->
                async {
                    saveRecoveryCode(recoveryCode, decryptedKekBytes)
                }
            }
            val deferredMasterKey: Deferred<Unit> = async {
                saveMasterKey(decryptedKekBytes)
            }
            val deferredBiometrics: Deferred<Unit> = async {
                if (session.useBiometrics) {
                    saveBiometricsKey(decryptedKekBytes)
                }
            }

            //Await all async operations:
            awaitAll(*deferredRecoveryCodes.toTypedArray(), deferredMasterKey, deferredBiometrics)

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
        val encryptedKekBytes: ByteArray = sourceKeyService.encryptKekWithSource(masterPassword, salt, decryptedKekBytes, true)

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
        val encryptedKekBytes: ByteArray = sourceKeyService.encryptKekWithSource(recoveryCode, salt, decryptedKekBytes, true)

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
     * Generates a new hardware-backed key that is only released after biometric auth. Then, the
     * KEK is encrypted using this key and stored in the repository.
     *
     * @param decryptedKekBytes Decrypted KEK.
     */
    private suspend fun saveBiometricsKey(decryptedKekBytes: ByteArray) = coroutineScope {
        val keyAlias: String = SecurityAliases.BiometricsHardwareBackedKey.getAlias()
        if (hardwareBackedKeyRepository.containsKey(keyAlias)) {
            hardwareBackedKeyRepository.deleteKey(keyAlias)
        }

        //10s timeout until key is locked again after biometric auth:
        val keyGenParameterSpec: KeyGenParameterSpec = keyGeneratorService.getKeyGenParameterSpecForSecureKey(keyAlias, 10)

        val biometricsKey: SecretKey = hardwareBackedKeyRepository.generateNewKey(
            alias = keyAlias,
            algorithm = KeyProperties.KEY_ALGORITHM_AES,
            keyGenParameterSpec = keyGenParameterSpec
        )

        val encryptedKekBytes: ByteArray = try {
            cipherService.encrypt(decryptedKekBytes, biometricsKey)
        } catch (e: Exception) {
            throw AuthSetupException("Cannot encrypt KEK using biometrics key: ${e.message ?: "Unknown error"}")
        }

        authRepository.setBiometricsKek(encryptedKekBytes)
    }

}
