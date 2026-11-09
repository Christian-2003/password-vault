package de.christian2003.feature.export.application.usecases

import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ServiceProgress
import de.christian2003.feature.export.domain.services.ServiceLauncherService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Use case to launch an export service suspended.
 *
 * @param exportLauncherService Service to launch an import service.
 */
internal class LaunchExportUseCase @Inject constructor(
    private val exportLauncherService: ServiceLauncherService
) {

    /**
     * Launches the export service with the specified ID suspended. The config is used to specify
     * the service parameters.
     *
     * @param id        ID of the export service to launch suspended.
     * @param config    Config for the service.
     * @return          Service progress.
     */
    fun launchExport(id: String, config: ExportConfig): Flow<ServiceProgress> {
        exportLauncherService.launchExportSuspended(id, config)
        return exportLauncherService.observeProgress(id)
    }

}
