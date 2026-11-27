package de.christian2003.passwordvault.application

import de.christian2003.passwordvault.domain.repositories.UpdateRepository
import javax.inject.Inject


/**
 * Use case to check whether an app update is available.
 *
 * @param updateRepository  Repository for app updates.
 */
internal class IsUpdateAvailableUseCase @Inject constructor(
    private val updateRepository: UpdateRepository
) {

    /**
     * Checks whether an app update is available.
     *
     * @return  Whether an update is available.
     */
    suspend fun isUpdateAvailable(): Boolean {
        return updateRepository.isUpdateAvailable()
    }

}
