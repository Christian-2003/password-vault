package de.christian2003.passwordvault.application.usecases.auth

import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import de.christian2003.passwordvault.application.repository.AuthRepository
import de.christian2003.passwordvault.application.security.BiometricAuthService
import javax.inject.Inject


/**
 * Use case to authenticate the user using biometrics.
 *
 * @param repository            Repository to access auth data.
 * @param biometricAuthService  Service facilitating biometric authentication.
 */
@ActivityScoped
class BiometricAuthUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val biometricAuthService: BiometricAuthService
) {

    /**
     * Authenticates the user using biometric authentication. If biometrics are unavailable or not
     * configured, this returns false.
     *
     * @return  Whether biometric authentication was successful.
     */
    suspend fun authenticate(): Boolean {
        if (repository.doesDeviceSupportBiometrics() && repository.hasBiometrics()) {
            return biometricAuthService.authenticate()
        }
        return false
    }

}
