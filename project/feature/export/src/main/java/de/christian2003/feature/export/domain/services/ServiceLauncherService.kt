package de.christian2003.feature.export.domain.services

import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ServiceProgress
import kotlinx.coroutines.flow.Flow


internal interface ServiceLauncherService {

    fun launchExportSuspended(id: String, config: ExportConfig)

    fun launchImportSuspended(id: String, config: ImportConfig)

    fun observeProgress(id: String): Flow<ServiceProgress>

}
