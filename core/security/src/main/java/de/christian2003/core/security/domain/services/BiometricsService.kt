package de.christian2003.core.security.domain.services


/**
 * Service for device biometrics.
 */
internal interface BiometricsService {

    /**
     * Shows a biometric prompt to the user with which to authenticate.
     *
     * @return  Whether biometric authentication was successful.
     */
    suspend fun authenticate(): Boolean

}
