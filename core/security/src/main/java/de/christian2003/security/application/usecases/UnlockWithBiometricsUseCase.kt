package de.christian2003.security.application.usecases

import dagger.hilt.android.scopes.ActivityScoped
import de.christian2003.security.domain.entities.SecurityAliases
import de.christian2003.security.domain.exceptions.UnlockFailedException
import de.christian2003.security.domain.repositories.BiometricsRepository
import de.christian2003.security.domain.repositories.HardwareBackedKeyRepository
import de.christian2003.security.domain.repositories.MasterKeyRepository
import de.christian2003.security.domain.repositories.UnlockedMasterKeyRepository
import de.christian2003.security.domain.services.BiometricsService
import de.christian2003.security.domain.services.CipherService
import javax.crypto.SecretKey
import javax.inject.Inject


/**
 * Use case to unlock the master key using biometrics.
 *
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 * @param biometricsRepository          Repository to access the KEK for biometrics.
 * @param masterKeyRepository           Repository to access the encrypted MK material.
 * @param unlockedMasterKeyRepository   Repository to set the unlocked MK after unlocking.
 * @param biometricsService             Service for biometric authentication.
 * @param cipherService                 Service for cryptographic operations.
 */
@ActivityScoped
class UnlockWithBiometricsUseCase @Inject constructor(
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository,
    private val biometricsRepository: BiometricsRepository,
    private val masterKeyRepository: MasterKeyRepository,
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
        val encryptedKekBytes: ByteArray? = biometricsRepository.getEncryptedBiometricsKek()
        if (encryptedKekBytes == null) {
            throw UnlockFailedException("Cannot unlock with biometrics, because KEK is unavailable")
        }

        //Authenticate:
        if (!biometricsRepository.areBiometricsAvailable()) {
            return false
        }
        val authResult: Boolean = biometricsService.authenticate()
        if (!authResult) {
            //Biometric auth failed:
            return false
        }

        //Get biometrics key:
        val biometricsKey: SecretKey? = hardwareBackedKeyRepository.getKey(SecurityAliases.BiometricsHardwareBackedKey.getAlias())
        if (biometricsKey == null) {
            throw UnlockFailedException("Cannot unlock with biometrics, because hardware-backed biometrics key is unavailable")
        }

        //Decrypt KEK:
        val decryptedKek: ByteArray = try {
            cipherService.decrypt(encryptedKekBytes, biometricsKey)
        } catch (e: Exception) {
            encryptedKekBytes.fill(0)
            throw UnlockFailedException("Cannot decrypt KEK (${e.message ?: "Unknown error"})")
        }

        //Get encrypted MK:
        val encryptedMkBytes: ByteArray? = masterKeyRepository.getEncryptedMasterKey()
        if (encryptedMkBytes == null) {
            encryptedKekBytes.fill(0)
            decryptedKek.fill(0)
            throw UnlockFailedException("Biometric auth failed because master key is unavailable")
        }

        //Decrypt MK:
        val decryptedMkBytes: ByteArray = try {
            cipherService.decrypt(encryptedMkBytes, decryptedKek)
        } catch (e: Exception) {
            encryptedKekBytes.fill(0)
            decryptedKek.fill(0)
            encryptedMkBytes.fill(0)
            throw UnlockFailedException("Cannot decrypt MK (${e.message ?: "Unknown error"})")
        }

        //Save unlocked master key:
        if (!unlockedMasterKeyRepository.isMasterKeyUnlocked()) {
            unlockedMasterKeyRepository.setUnlockedMasterKeyBytes(decryptedMkBytes)
        }

        encryptedKekBytes.fill(0)
        decryptedKek.fill(0)
        encryptedMkBytes.fill(0)

        return true
    }

}
