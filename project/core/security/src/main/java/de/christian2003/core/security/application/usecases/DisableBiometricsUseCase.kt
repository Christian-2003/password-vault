package de.christian2003.core.security.application.usecases

import de.christian2003.core.security.domain.entities.SecurityAliases
import de.christian2003.core.security.domain.repositories.AuthTransactionRepository
import de.christian2003.core.security.domain.repositories.HardwareBackedKeyRepository
import javax.inject.Inject


/**
 * Use case to disable the biometric authentication.
 *
 * @param authRepository                Repository to change auth data.
 * @param hardwareBackedKeyRepository   Repository to access hardware-backed keys.
 */
class DisableBiometricsUseCase @Inject internal constructor(
    private val authRepository: AuthTransactionRepository,
    private val hardwareBackedKeyRepository: HardwareBackedKeyRepository
) {

    /**
     * Disables the biometric authentication.
     */
    fun disable() {
        try {
            authRepository.beginTransaction()
            authRepository.deleteBiometricsKek()
            authRepository.commitTransaction()
            hardwareBackedKeyRepository.deleteKey(SecurityAliases.BiometricsHardwareBackedKey.getAlias())
        }
        catch (_: Exception) { }
    }

}
