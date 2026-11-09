package de.christian2003.feature.export.application.usecases

import de.christian2003.feature.export.domain.entities.ServiceProgress
import de.christian2003.feature.export.domain.services.ServiceLauncherService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Use case to observe the progress of an import / export service.
 *
 * @param serviceLauncherService    Service used for launching import / export service.
 */
internal class ObserveServiceProgressUseCase @Inject constructor(
    private val serviceLauncherService: ServiceLauncherService
) {

    /**
     * Observes the progress for the specified import / export service.
     *
     * @param id    ID of the import / export service to observe.
     * @return      Service progress.
     */
    fun observeProgress(id: String): Flow<ServiceProgress> {
        return serviceLauncherService.observeProgress(id)
    }

}
