package de.christian2003.feature.export.infrastructure.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.services.ImportService
import de.christian2003.feature.export.infrastructure.work.dto.ImportConfigDto
import de.christian2003.feature.export.infrastructure.work.mapper.ImportConfigMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json


@HiltWorker
internal class ImportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val services: Set<@JvmSuppressWildcards ImportService>,
    private val importConfigMapper: ImportConfigMapper
): CoroutineWorker(context, params) {

    val json: Json = Json


    override suspend fun doWork(): Result {
        try {
            //Get input data:
            val id: String? = inputData.getString("import_id")
            val configJson: String? = inputData.getString("import_config")

            if (id == null || configJson == null) {
                return Result.failure()
            }

            val configDto: ImportConfigDto = json.decodeFromString<ImportConfigDto>(configJson)
            val config: ImportConfig = importConfigMapper.toDomain(configDto)

            //Get export service:
            val service: ImportService? = services.firstOrNull { it.importDescriptor.id == id }
            if (service == null) {
                return Result.failure()
            }

            //Launch work:
            val progressFlow: Flow<Float> = service.beginImport(config)
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
