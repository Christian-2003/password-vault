package de.christian2003.core.security.application.usecases

import dagger.hilt.android.scopes.ActivityScoped
import de.christian2003.core.security.domain.entities.SecurityAliases
import de.christian2003.core.security.domain.exceptions.UnlockFailedException
import de.christian2003.core.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.core.security.domain.repositories.ReadonlyAuthRepository
import de.christian2003.core.security.domain.repositories.UnlockedMasterKeyRepository
import de.christian2003.core.security.domain.services.BiometricsService
import de.christian2003.core.security.domain.services.CipherService
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Use case to unlock the master key using biometrics.
 *
 * @param readonlyAuthRepository        Repository to access auth data.
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 * @param unlockedMasterKeyRepository   Repository to set the unlocked MK after unlocking.
 * @param biometricsService             Service for biometric authentication.
 * @param cipherService                 Service for cryptographic operations.
 */
@ActivityScoped
class UnlockWithBiometricsUseCase @Inject internal constructor(
    private val readonlyAuthRepository: ReadonlyAuthRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val unlockedMasterKeyRepository: UnlockedMasterKeyRepository,
    private val biometricsService: BiometricsService,
    private val cipherService: CipherService
) {

    /**
     * Unlocks the master key using biometrics.
     *
     * @return                          Whether the master key was unlocked successfully, or whether
     *                                  biometric authentication failed.
     * @throws UnlockFailedException    Cannot unlock master key with biometrics.
     */
    suspend fun unlock(): Boolean {
        //Get encrypted KEK:
        val biometricsKey: SecretKey? = hardwareBackedKeyRepository.getKey(SecurityAliases.BiometricsHardwareBackedKey.getAlias())
        val encryptedKekBytes: ByteArray? = readonlyAuthRepository.getBiometricsKek()
        val encryptedMasterKeyBytes: ByteArray? = readonlyAuthRepository.getEncryptedMasterKey()
        var decryptedKekBytes: ByteArray? = null
        var decryptedMasterKeyBytes: ByteArray?

        try {
            if (biometricsKey == null || encryptedKekBytes == null || encryptedMasterKeyBytes == null) {
                throw UnlockFailedException("Biometrics have not yet been set up")
            }

            //Authenticate:
            if (!readonlyAuthRepository.isBiometricsAvailable()) {
                return false
            }
            val authResult: Boolean = biometricsService.authenticate()
            if (!authResult) {
                //Biometric auth failed:
                return false
            }

            //Decrypt KEK:
            decryptedKekBytes = try {
                cipherService.decrypt(encryptedKekBytes, biometricsKey)
            } catch (_: Exception) {
                return false
            }

            //Decrypt MK:
            decryptedMasterKeyBytes = try {
                cipherService.decrypt(encryptedMasterKeyBytes, decryptedKekBytes)
            } catch (_: Exception) {
                return false
            }

            unlockedMasterKeyRepository.setUnlockedMasterKeyBytes(decryptedMasterKeyBytes)
            return true
        }
        finally {
            encryptedKekBytes?.fill(0)
            encryptedMasterKeyBytes?.fill(0)
            decryptedKekBytes?.fill(0)
        }
    }

}
