package de.christian2003.security.application.usecases

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dagger.hilt.android.scopes.ActivityScoped
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.exceptions.AuthSetupException
import de.christian2003.security.domain.repositories.BiometricsRepository
import de.christian2003.security.domain.repositories.DecryptedKekRepository
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.services.BiometricsService
import de.christian2003.security.domain.services.CipherService
import de.christian2003.security.domain.services.KeyGeneratorService
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Use case for the setup of the biometric authentication.
 *
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 * @param decryptedKekRepository        Repository to access the decrypted KEK during setup.
 * @param biometricsRepository          Repository to access the KEK for biometric unlock.
 * @param biometricsService             Service to facilitate biometric operations.
 * @param keyGeneratorService           Service to generate cryptographic keys.
 * @param cipherService                 Service to facilitate cryptographic operations, such as AES.
 */
@ActivityScoped
class SetupBiometricsUseCase @Inject constructor(
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val decryptedKekRepository: DecryptedKekRepository,
    private val biometricsRepository: BiometricsRepository,
    private val biometricsService: BiometricsService,
    private val keyGeneratorService: KeyGeneratorService,
    private val cipherService: CipherService
) {

    /**
     * Setup for the biometric authentication.
     *
     * @throws AuthSetupException   Biometric authentication cannot be setup.
     */
    suspend fun setupBiometrics() {
        //Get decrypted KEK:
        val decryptedKekBytes: ByteArray? = decryptedKekRepository.getDecryptedKek()
        if (decryptedKekBytes == null) {
            throw AuthSetupException("Cannot setup biometric authentication because there is no KEK available")
        }

        //Biometric authentication:
        if (!biometricsRepository.areBiometricsAvailable()) {
            throw AuthSetupException("Cannot setup biometric authentication because biometrics are unavailable on the device")
        }
        val authenticationResult: Boolean = biometricsService.authenticate()
        if (!authenticationResult) {
            throw AuthSetupException("Cannot setup biometrics because authentication failed")
        }

        //Get key for authentication:
        val key: SecretKey = generateHardwareBackedKey()

        //Encrypt KEK:
        val encryptedKekBytes: ByteArray = try {
            cipherService.encrypt(decryptedKekBytes, key)
        } catch (_: Exception) {
            throw AuthSetupException("Cannot setup biometric authentication because KEK cannot be encrypted")
        }

        //Save key in repository:
        biometricsRepository.setEncryptedBiometricsKek(encryptedKekBytes)
    }


    /**
     * Generates a new hardware-backed key that is only released upon biometric authentication for
     * 30 seconds. If a key already exists, it is removed beforehand, since this method will always
     * generate a new key.
     *
     * @return  Generated secret key that is only released after biometric authentication.
     */
    private fun generateHardwareBackedKey(): SecretKey {
        val alias = SecurityAliases.BiometricsHardwareBackedKey.getAlias()
        if (hardwareBackedKeyRepository.containsKey(alias)) {
            hardwareBackedKeyRepository.deleteKey(alias)
        }

        val timeout = 30 //Since the key only needs to be unlocked to decrypt the KEK, a short time period should be sufficient
        val keyGenParameterSpec: KeyGenParameterSpec = keyGeneratorService.getKeyGenParameterSpecForSecureKey(
            alias = alias,
            timeout = timeout
        )

        val key: SecretKey = hardwareBackedKeyRepository.generateNewKey(
            alias = alias,
            algorithm = KeyProperties.KEY_ALGORITHM_AES,
            keyGenParameterSpec = keyGenParameterSpec
        )

        return key
    }

}
