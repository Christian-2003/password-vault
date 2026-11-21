package de.christian2003.feature.export.infrastructure.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.services.ExportService
import de.christian2003.feature.export.infrastructure.work.dto.ExportConfigDto
import de.christian2003.feature.export.infrastructure.work.mapper.ExportConfigMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json


/**
 * Export worker handles the export of files in a Worker.
 *
 * @param context               Context.
 * @param params                Worker parameters.
 * @param services              Set of all export services.
 * @param exportConfigMapper    Mapper for the export config.
 */
@HiltWorker
internal class ExportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val services: Set<@JvmSuppressWildcards ExportService>,
    private val exportConfigMapper: ExportConfigMapper
): CoroutineWorker(context, params) {

    /**
     * JSON used to deserialize the export config.
     */
    val json: Json = Json


    /**
     * Performs the work inside the worker.
     *
     * @return  Result of the work performed.
     */
    override suspend fun doWork(): Result {
        try {
            //Get input data:
            val id: String? = inputData.getString("export_id")
            val configJson: String? = inputData.getString("export_config")

            if (id == null || configJson == null) {
                return Result.failure()
            }

            val configDto: ExportConfigDto = json.decodeFromString<ExportConfigDto>(configJson)
            val config: ExportConfig = exportConfigMapper.toDomain(configDto)

            //Get export service:
            val service: ExportService? = services.firstOrNull { it.exportDescriptor.id == id }
            if (service == null) {
                return Result.failure()
            }

            //Launch work:
            val progressFlow: Flow<Float> = service.createExport(config)
            progressFlow.collect { progress ->
                setProgress(workDataOf("progress" to progress))
                Log.d("ExportService", "Emit progress ${progress ?: "-/-"}")
            }

            Log.d("ExportService", "Finish service")
            return Result.success()
        }
        catch (_: Exception) {
            Log.d("ExportService", "Fail service")
            return Result.failure()
        }
    }

}
