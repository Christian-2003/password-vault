package de.christian2003.feature.export.application.usecases

import de.christian2003.feature.export.domain.entities.ExportProgress
import de.christian2003.feature.export.domain.services.ExportLauncherService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


internal class ObserveExportProgressUseCase @Inject constructor(
    private val exportLauncherService: ExportLauncherService
) {

    fun observeProgress(id: String): Flow<ExportProgress> {
        return exportLauncherService.observeProgress(id)
    }

}
