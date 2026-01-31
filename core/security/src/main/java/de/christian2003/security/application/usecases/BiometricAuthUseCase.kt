package de.christian2003.security.application.usecases

import dagger.hilt.android.scopes.ActivityScoped
import de.christian2003.security.domain.services.BiometricsService
import javax.inject.Inject


/**
 * Use case to perform biometric authentication.
 *
 * @param biometricsService Service for biometric authentication.
 */
@ActivityScoped
class BiometricAuthUseCase @Inject constructor(
    private val biometricsService: BiometricsService
) {

    /**
     * Authenticates using biometrics.
     *
     * @return  Whether biometric auth was successful.
     */
    suspend fun authenticate(): Boolean {
        return try {
            biometricsService.authenticate()
        } catch (_: Exception) {
            false
        }
    }

}
