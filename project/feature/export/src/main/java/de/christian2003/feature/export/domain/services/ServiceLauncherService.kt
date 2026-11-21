package de.christian2003.feature.export.domain.services

import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ServiceProgress
import kotlinx.coroutines.flow.Flow


/**
 * Service that can launch an import or export suspended.
 * The imports and exports are launched suspended by concept of fire-and-forget. Progress of the
 * launched services can be observed afterward.
 */
internal interface ServiceLauncherService {

    /**
     * Launches an export service.
     *
     * @param id        ID of the export service to launch.
     * @param config    Export config.
     */
    fun launchExportSuspended(id: String, config: ExportConfig)

    /**
     * Launches an import service.
     *
     * @param id        ID of the import service to launch.
     * @param config    Import config.
     */
    fun launchImportSuspended(id: String, config: ImportConfig)

    /**
     * Observes the progress of the specified export / import service.
     *
     * @param id    ID of the service whose progress to observe.
     * @return      Flow containing the service progress of a running service.
     * @return      Flow containing the service progress of a running service.
     */
    fun observeProgress(id: String): Flow<ServiceProgress>

}
