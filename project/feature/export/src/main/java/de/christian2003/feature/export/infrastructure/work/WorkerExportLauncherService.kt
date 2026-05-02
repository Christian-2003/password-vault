package de.christian2003.feature.export.infrastructure.work

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ExportProgress
import de.christian2003.feature.export.domain.entities.ProgressState
import de.christian2003.feature.export.domain.services.ExportLauncherService
import de.christian2003.feature.export.infrastructure.work.dto.ExportConfigDto
import de.christian2003.feature.export.infrastructure.work.mapper.ExportConfigMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject


internal class WorkerExportLauncherService @Inject constructor(
    private val exportConfigMapper: ExportConfigMapper,
    private val workManager: WorkManager
): ExportLauncherService {

    private val json: Json = Json {  }


    override fun launchSuspended(id: String, config: ExportConfig) {
        //Serialize config because WorkManager only accepts primitive types and strings:
        val exportConfigDto: ExportConfigDto = exportConfigMapper.toDto(config)
        val exportConfigJson: String = json.encodeToString<ExportConfigDto>(exportConfigDto)

        val inputData: Data = workDataOf(
            "export_id" to id,
            "export_config" to exportConfigJson
        )

        val request = OneTimeWorkRequestBuilder<ExportWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.KEEP, request)
    }


    override fun observeProgress(id: String): Flow<ExportProgress> {
        return workManager
            .getWorkInfosForUniqueWorkFlow(id)
            .map { workInfos ->
                val workInfo: WorkInfo? = workInfos.firstOrNull()

                if (workInfo == null) {
                    val progress = ExportProgress(
                        progress = 0f,
                        state = ProgressState.Enqueued
                    )
                    return@map progress
                }

                val workerProgress: Float = workInfo.progress.getFloat("progress", 0f)

                val progress = ExportProgress(
                    progress = workerProgress,
                    state = when {
                        workInfo.state == WorkInfo.State.RUNNING -> ProgressState.Running
                        workInfo.state == WorkInfo.State.SUCCEEDED -> ProgressState.Finished
                        workInfo.state == WorkInfo.State.FAILED -> ProgressState.Failed
                        else -> ProgressState.Enqueued
                    }
                )

                return@map progress
            }
    }

}
