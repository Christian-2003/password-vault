package de.christian2003.feature.export.application.usecases

import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ServiceProgress
import de.christian2003.feature.export.domain.services.ServiceLauncherService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Use case to launch an import service suspended.
 *
 * @param importLauncherService Service to launch an import service.
 */
internal class LaunchImportUseCase @Inject constructor(
    private val importLauncherService: ServiceLauncherService
) {

    /**
     * Launches the import service with the specified ID suspended. The config is used to specify
     * the service parameters.
     *
     * @param id        ID of the import service to launch suspended.
     * @param config    Config for the service.
     * @return          Service progress.
     */
    fun launchImport(id: String, config: ImportConfig): Flow<ServiceProgress> {
        importLauncherService.launchImportSuspended(id, config)
        return importLauncherService.observeProgress(id)
    }

}
