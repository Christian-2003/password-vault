package de.christian2003.feature.export.domain.services

import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ExportDescriptor
import kotlinx.coroutines.flow.Flow


/**
 * Service through which to create exports of data.
 */
internal interface ExportService {

    /**
     * Descriptor for the export service.
     */
    val exportDescriptor: ExportDescriptor


    /**
     * Creates an export according to the provided configuration. The method starts a background
     * task and provides progress updates using the returned flow, which reports the progress as
     * a percentage between 0 % to 100 % (i.e. 0.0 to 1.0).
     *
     * @param config    Configuration for the export.
     * @return          Flow which reports the progress.
     */
    fun createExport(config: ExportConfig): Flow<Float>

}
