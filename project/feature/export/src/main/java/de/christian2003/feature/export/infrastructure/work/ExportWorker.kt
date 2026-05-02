package de.christian2003.feature.export.infrastructure.work

import android.content.Context
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


@HiltWorker
internal class ExportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val services: Set<@JvmSuppressWildcards ExportService>,
    private val exportConfigMapper: ExportConfigMapper
): CoroutineWorker(context, params) {

    val json: Json = Json {  }

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
            }

            return Result.success()
        }
        catch (_: Exception) {
            return Result.failure()
        }
    }

}
