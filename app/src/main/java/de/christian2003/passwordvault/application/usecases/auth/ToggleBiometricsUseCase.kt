package de.christian2003.passwordvault.application.usecases.auth

import dagger.hilt.android.scopes.ActivityScoped
import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.application.security.BiometricAuthService
import javax.inject.Inject


/**
 * Use case to enable and disable the biometrics.
 *
 * @param repository            Repository to access auth information.
 * @param biometricAuthService  Service facilitating biometric authentication.
 */
@ActivityScoped
class ToggleBiometricsUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val biometricAuthService: BiometricAuthService
) {

    /**
     * Enables / disables the biometric authentication.
     *
     * @return  Whether biometric authentication was toggled successfully.
     */
    suspend fun toggleBiometrics(): Boolean {
        if (repository.doesDeviceSupportBiometrics()) {
            //Biometrics available:
            val result: Boolean = biometricAuthService.authenticate()
            if (result) {
                val isEnabled: Boolean = repository.hasBiometrics()
                repository.setBiometrics(!isEnabled)
                return true
            }
        }
        return false
    }

}
