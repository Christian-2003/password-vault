package de.christian2003.security.application.usecases

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import de.christian2003.security.application.services.SourceKeyService
import de.christian2003.security.domain.entities.EnableBiometricsSession
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.exceptions.UnlockFailedException
import de.christian2003.security.domain.exceptions.UnlockSourceInvalidException
import de.christian2003.security.domain.repositories.AuthTransactionRepository
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KeyGeneratorService
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Use case to save the session data to enable the biometric authentication.
 * The use case expects that successful biometric authentication happened beforehand!
 *
 * @param authRepository                Repository to change auth data.
 * @param readonlyAuthRepository        Repository to read auth data.
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 * @param sourceKeyService              Service for source key handling.
 * @param keyGeneratorService           Service for key generation.
 * @param cipherService                 Service for cryptographic operations.
 */
class SaveEnableBiometricsSessionUseCase @Inject internal constructor(
    private val authRepository: AuthTransactionRepository,
    private val readonlyAuthRepository: ReadonlyAuthRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val sourceKeyService: SourceKeyService,
    private val keyGeneratorService: KeyGeneratorService,
    private val cipherService: CipherService,
) {

    /**
     * Saves the provided session data to enable biometric auth.
     *
     * @param session   Session data to save.
     */
    suspend fun save(session: EnableBiometricsSession) {
        if (!readonlyAuthRepository.isBiometricsAvailable()) {
            throw AuthSetupException("Biometrics are unavailable")
        }
        if (readonlyAuthRepository.isBiometricsConfigured()) {
            //Authentication already configured
            return
        }
        var decryptedKekBytes: ByteArray? = null

        try {
            authRepository.beginTransaction()

            //Get decrypted KEK:
            decryptedKekBytes = getDecryptedKekFromMasterPassword(session.masterPassword)

            //Save KEK for biometric login:
            saveBiometricsKey(decryptedKekBytes)

            authRepository.commitTransaction()
        }
        finally {
            decryptedKekBytes?.fill(0)
        }
    }


    /**
     * Retrieves the decrypted KEK from the specified master password. If a decrypted KEK cannot be
     * obtained, an exception is thrown.
     *
     * @param masterPassword                Master password from which to obtain the decrypted KEK.
     * @return                              Bytes of the decrypted KEK.
     * @throws UnlockSourceInvalidException The master password is invalid.
     * @throws UnlockFailedException        The KEK cannot be decrypted.
     * @throws AuthSetupException           The master password has not yet been set up.
     */
    private suspend fun getDecryptedKekFromMasterPassword(masterPassword: CharArray): ByteArray {
        val saltBytes: ByteArray? = readonlyAuthRepository.getMasterPasswordSalt()
        val encryptedKekBytes: ByteArray? = readonlyAuthRepository.getMasterPasswordKek()
        var decryptedKekBytes: ByteArray?

        try {
            if (saltBytes == null || encryptedKekBytes == null) {
                throw UnlockFailedException("Master password is not set up")
            }

            //KEK was decrypted successfully:
            decryptedKekBytes = sourceKeyService.decryptKekWithSource(encryptedKekBytes, masterPassword, saltBytes)
            return decryptedKekBytes
        }
        finally {
            saltBytes?.fill(0)
            encryptedKekBytes?.fill(0)
        }
    }


    /**
     * Generates a new hardware-backed key that is only released after biometric auth. Then, the
     * KEK is encrypted using this key and stored in the repository.
     *
     * @param decryptedKekBytes Decrypted KEK.
     */
    private suspend fun saveBiometricsKey(decryptedKekBytes: ByteArray) {
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
