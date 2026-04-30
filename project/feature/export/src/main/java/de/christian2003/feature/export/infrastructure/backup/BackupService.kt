package de.christian2003.feature.export.infrastructure.backup

import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.services.ExportService
import de.christian2003.feature.export.infrastructure.backup.v3.V3BackupService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


internal class BackupService @Inject constructor(
    private val v3BackupService: V3BackupService
) : ExportService {

    /**
     * Creates an export according to the provided configuration. The method starts a background
     * task and provides progress updates using the returned flow, which reports the progress as
     * a percentage between 0 % to 100 % (i.e. 0.0 to 1.0).
     *
     * @param config    Configuration for the export.
     * @return          Flow which reports the progress.
     */
    override fun createExport(config: ExportConfig): Flow<Float> {
        return v3BackupService.createExport(config)
    }

}
