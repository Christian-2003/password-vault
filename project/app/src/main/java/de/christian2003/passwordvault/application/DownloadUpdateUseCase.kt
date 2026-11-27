package de.christian2003.passwordvault.application

import de.christian2003.passwordvault.domain.repositories.UpdateRepository
import javax.inject.Inject


/**
 * Use case to request a download of the newest app version.
 *
 * @param updateRepository  Repository for app updates.
 */
internal class DownloadUpdateUseCase @Inject constructor(
    private val updateRepository: UpdateRepository
) {

    /**
     * Requests to download the newest version.
     */
    suspend fun requestDownload() {
        updateRepository.requestDownload()
    }

}
