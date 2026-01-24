package de.christian2003.security.application.usecases

import de.christian2003.security.domain.repositories.BiometricsRepository
import de.christian2003.security.domain.repositories.CommitRepository
import javax.inject.Inject


/**
 * Use case to test whether biometrics are configured and can be used to unlock the master key.
 *
 * @param biometricsRepository  Repository to access biometrics data.
 * @param commitRepository      Repository to check whether changes to authentication are staged.
 */
class AreBiometricsConfiguredUseCase @Inject constructor(
    private val biometricsRepository: BiometricsRepository,
    private val commitRepository: CommitRepository
) {

    /**
     * Returns whether the biometrics are configured and can be used for unlocking the master key.
     *
     * @return  Whether biometrics are configured and can unlock the master key.
     */
    fun areBiometricsConfigured(): Boolean {
        return biometricsRepository.areBiometricsAvailable()
                && biometricsRepository.hasEncryptedBiometricsKek()
                && !commitRepository.areChangesStaged()
    }

}
