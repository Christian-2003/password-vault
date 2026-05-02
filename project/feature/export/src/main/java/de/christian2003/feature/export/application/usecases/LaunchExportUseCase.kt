package de.christian2003.feature.export.application.usecases

import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ExportProgress
import de.christian2003.feature.export.domain.services.ExportLauncherService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


internal class LaunchExportUseCase @Inject constructor(
    private val exportLauncherService: ExportLauncherService
) {

    fun launchExport(id: String, config: ExportConfig): Flow<ExportProgress> {
        exportLauncherService.launchSuspended(id, config)
        return exportLauncherService.observeProgress(id)
    }

}
