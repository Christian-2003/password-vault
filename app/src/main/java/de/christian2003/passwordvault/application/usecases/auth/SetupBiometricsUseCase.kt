package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.application.security.BiometricAuthService


/**
 * Use case to enable and disable the biometrics.
 *
 * @param repository            Repository to access auth information.
 * @param biometricAuthService  Service facilitating biometric authentication.
 */
class SetupBiometricsUseCase(
    private val repository: AuthRepository,
    private val biometricAuthService: BiometricAuthService
) {

    /**
     * Enables the biometrics.
     *
     * @return  Whether biometrics were enabled successfully.
     */
    suspend fun enableBiometrics(): Boolean {
        return if (!repository.hasBiometrics()) {
            setBiometrics(true)
        } else {
            false
        }
    }


    /**
     * Disables the biometrics.
     *
     * @return  Whether biometrics were disabled successfully.
     */
    suspend fun disableBiometrics(): Boolean {
        return if (repository.hasBiometrics()) {
            setBiometrics(false)
        } else {
            false
        }
    }


    /**
     * Enables or disables biometrics after using authentication to verify the user's identity.
     *
     * @param enabled   Whether to enable (= true) or disable (= false) biometrics.
     * @return          Whether biometrics was successfully enabled or disabled.
     */
    private suspend fun setBiometrics(enabled: Boolean): Boolean {
        if (repository.doesDeviceSupportBiometrics()) {
            //Biometrics available:
            val result: Boolean = biometricAuthService.authenticate()
            if (result) {
                repository.setBiometrics(enabled)
                return true
            }
        }
        return false
    }

}
