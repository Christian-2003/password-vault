package de.christian2003.security.application.usecases

import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import javax.inject.Inject


/**
 * Use case to test whether biometrics are available on the device.
 *
 * @param readonlyAuthRepository    Repository to read auth data.
 */
class AreBiometricsAvailableUseCase @Inject internal constructor(
    private val readonlyAuthRepository: ReadonlyAuthRepository
) {

    /**
     * Returns whether biometrics are available on the device.
     *
     * @return  Whether biometrics are available.
     */
    fun areBiometricsAvailable(): Boolean {
        return readonlyAuthRepository.isBiometricsAvailable()
    }

}
