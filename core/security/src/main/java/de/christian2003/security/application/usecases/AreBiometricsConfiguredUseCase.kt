package de.christian2003.security.application.usecases

import de.christian2003.security.domain.repositories.ReadonlyAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject


/**
 * Use case to test whether biometrics are configured and can be used to unlock the master key.
 *
 * @param readonlyAuthRepository    Repository to access auth data.
 */
class AreBiometricsConfiguredUseCase @Inject internal constructor(
    private val readonlyAuthRepository: ReadonlyAuthRepository
) {

    /**
     * Returns whether the biometrics are configured and can be used for unlocking the master key.
     *
     * @return  Whether biometrics are configured and can unlock the master key.
     */
    fun areBiometricsConfigured(): Boolean {
        return readonlyAuthRepository.isBiometricsConfigured() && readonlyAuthRepository.isBiometricsAvailable()
    }


    /**
     * Returns whether the biometrics are configured and can be used for unlocking the master key.
     *
     * @return  Whether biometrics are configured and can unlock the master key.
     */
    fun areBiometricsConfiguredAsFlow(): Flow<Boolean> {
        val isAvailable: Boolean = readonlyAuthRepository.isBiometricsAvailable()
        val isConfiguredFlow: Flow<Boolean> = readonlyAuthRepository.isBiometricsConfiguredAsFlow()

        val result: Flow<Boolean> = isConfiguredFlow.map { isConfigured ->
            isConfigured && isAvailable
        }.distinctUntilChanged()

        return result
    }

}
