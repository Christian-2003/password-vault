package de.christian2003.feature.export.domain.services

import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ExportProgress
import kotlinx.coroutines.flow.Flow


internal interface ExportLauncherService {

    fun launchSuspended(id: String, config: ExportConfig)

    fun observeProgress(id: String): Flow<ExportProgress>

}
