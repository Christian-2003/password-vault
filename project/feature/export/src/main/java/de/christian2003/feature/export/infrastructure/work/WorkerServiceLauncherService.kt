package de.christian2003.feature.export.infrastructure.work

import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ProgressErrorCode
import de.christian2003.feature.export.domain.entities.ServiceProgress
import de.christian2003.feature.export.domain.entities.ProgressState
import de.christian2003.feature.export.domain.services.ServiceLauncherService
import de.christian2003.feature.export.infrastructure.work.dto.ExportConfigDto
import de.christian2003.feature.export.infrastructure.work.dto.ImportConfigDto
import de.christian2003.feature.export.infrastructure.work.mapper.ExportConfigMapper
import de.christian2003.feature.export.infrastructure.work.mapper.ImportConfigMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject


/**
 * Service that can launch an import or export suspended.
 * The imports and exports are launched suspended by concept of fire-and-forget. Progress of the
 * launched services can be observed afterward.
 *
 * This implementation launches the services inside an Android Worker.
 *
 * @param exportConfigMapper    Mapper for export configs.
 * @param importConfigMapper    Mapper for import configs.
 * @param workManager           Android Work Manager.
 */
internal class WorkerServiceLauncherService @Inject constructor(
    private val exportConfigMapper: ExportConfigMapper,
    private val importConfigMapper: ImportConfigMapper,
    private val workManager: WorkManager
): ServiceLauncherService {

    /**
     * JSON used to serialize export and import configs.
     */
    private val json: Json = Json

    /**
     * Caches the service progresses for each service launched.
     */
    private val observedProgressCache: MutableMap<String, Flow<ServiceProgress>> = mutableMapOf()

    /**
     * Caches the worker ID and maps it to the corresponding service ID.
     */
    private val workerIdsCache: MutableMap<String, UUID> = mutableMapOf()


    /**
     * Launches an export service.
     *
     * @param id        ID of the export service to launch.
     * @param config    Export config.
     */
    override fun launchExportSuspended(id: String, config: ExportConfig) {
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

        workerIdsCache[id] = request.id

        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.KEEP, request)
    }


    /**
     * Launches an import service.
     *
     * @param id        ID of the import service to launch.
     * @param config    Import config.
     */
    override fun launchImportSuspended(id: String, config: ImportConfig) {
        //Serialize config because WorkManager only accepts primitive types and strings:
        val importConfigDto: ImportConfigDto = importConfigMapper.toDto(config)
        val importConfigJson: String = json.encodeToString<ImportConfigDto>(importConfigDto)

        val inputData: Data = workDataOf(
            "import_id" to id,
            "import_config" to importConfigJson
        )

        val request = OneTimeWorkRequestBuilder<ImportWorker>()
            .setInputData(inputData)
            .build()

        workerIdsCache[id] = request.id

        workManager.enqueueUniqueWork(id, ExistingWorkPolicy.KEEP, request)
    }


    /**
     * Observes the progress of the specified export / import service.
     *
     * @param id    ID of the service whose progress to observe.
     * @return      Flow containing the service progress of a running service.
     * @return      Flow containing the service progress of a running service.
     */
    override fun observeProgress(id: String): Flow<ServiceProgress> {
        var serviceProgress: Flow<ServiceProgress>? = observedProgressCache[id]

        if (serviceProgress != null) {
            return serviceProgress
        }

        Log.d("WorkerLauncherService", "Create new progress flow")

        val workerId: UUID? = workerIdsCache[id]

        if (workerId != null) {
            serviceProgress = workManager
                .getWorkInfoByIdFlow(workerId)
                .map { workInfo ->
                    var serviceProgress: ServiceProgress? = null

                    if (workInfo != null) {
                        when (workInfo.state) {
                            WorkInfo.State.RUNNING -> {
                                //Work in progress:
                                val progress: Float = workInfo.progress.getFloat("progress", -1f)

                                serviceProgress = ServiceProgress(
                                    progress = progress,
                                    state = ProgressState.Running,
                                    errorCode = ProgressErrorCode.Ok
                                )

                                Log.d("WorkerLauncherService", "Received RUNNING progress (Progress: '${progress}')")
                            }

                            WorkInfo.State.SUCCEEDED, WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                                //Work has finished:
                                val progress: Float = workInfo.outputData.getFloat("progress", 1f)
                                val errorMessage: String? = workInfo.outputData.getString("err_code")

                                val state: ProgressState = when (workInfo.state) {
                                    WorkInfo.State.SUCCEEDED -> ProgressState.Finished
                                    else -> ProgressState.Failed
                                }

                                val errorCode: ProgressErrorCode = when (errorMessage) {
                                    "key_invalid" -> ProgressErrorCode.InvalidKey
                                    "file_corrupt" -> ProgressErrorCode.CorruptFile
                                    "invalid_file_format" -> ProgressErrorCode.InvalidFileFormat
                                    else -> ProgressErrorCode.Ok
                                }

                                serviceProgress = ServiceProgress(
                                    progress = progress,
                                    state = state,
                                    errorCode = errorCode
                                )

                                Log.d("WorkerLauncherService", "Received ${workInfo.state} progress (Error: '${errorCode}' | '${errorMessage ?: "-"}')")
                            }

                            WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> {
                                //Waiting to start:
                                serviceProgress = ServiceProgress(
                                    progress = 0f,
                                    state = ProgressState.Enqueued,
                                    errorCode = ProgressErrorCode.Ok
                                )

                                Log.d("WorkerLauncherService", "Received ${workInfo.state} progress")
                            }
                        }
                    }


                    if (serviceProgress == null) {
                        serviceProgress = ServiceProgress(
                            progress = -1f,
                            state = ProgressState.None,
                            errorCode = ProgressErrorCode.Ok
                        )
                        Log.d("WorkerLauncherService", "Received no progress")
                    }

                    return@map serviceProgress
                }
        }

        if (serviceProgress == null) {
            val progress = ServiceProgress(
                progress = 0f,
                state = ProgressState.None,
                errorCode = ProgressErrorCode.Ok
            )
            serviceProgress = flowOf(progress)
        }

        observedProgressCache[id] = serviceProgress
        return serviceProgress
    }

}
