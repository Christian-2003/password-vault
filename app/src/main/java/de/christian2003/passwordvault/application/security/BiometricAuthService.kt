package de.christian2003.passwordvault.application.security


/**
 * Service for biometric authentication.
 */
interface BiometricAuthService {

    /**
     * Authenticates the user using biometric authentication.
     *
     * @return  Whether the user was authenticated successfully.
     */
    suspend fun authenticate(): Boolean

}
