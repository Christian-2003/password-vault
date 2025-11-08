package de.christian2003.passwordvault.application.usecases.auth

import de.christian2003.passwordvault.application.repository.AuthRepository
import javax.inject.Inject


/**
 * Use case to determine whether biometrics are available on the device.
 *
 * @param repository    Repository to access auth data.
 */
class AreBiometricsAvailableUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    /**
     * Returns whether biometrics are supported on the Android device.
     *
     * @return  Whether biometrics are available.
     */
    fun areBiometricsAvailable(): Boolean {
        return repository.doesDeviceSupportBiometrics()
    }

}
