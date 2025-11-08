package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import javax.inject.Inject


/**
 * Use case to query whether biometrics should be used for authentication.
 *
 * @param repository    Repository to access auth information.
 */
class AreBiometricsConfiguredUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Returns whether biometrics are configured for the app and should be used for authentication.
     *
     * @return  Whether to use biometrics for authentication.
     */
    fun areBiometricsConfigured(): Boolean {
        return repository.hasBiometrics() && repository.doesDeviceSupportBiometrics()
    }

}
