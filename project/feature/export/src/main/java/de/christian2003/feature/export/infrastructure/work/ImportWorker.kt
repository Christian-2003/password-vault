package de.christian2003.feature.export.infrastructure.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.exceptions.CorruptImportFileException
import de.christian2003.feature.export.domain.exceptions.InvalidKeyException
import de.christian2003.feature.export.domain.exceptions.UnsupportedFileFormatException
import de.christian2003.feature.export.domain.services.ImportService
import de.christian2003.feature.export.infrastructure.work.dto.ImportConfigDto
import de.christian2003.feature.export.infrastructure.work.mapper.ImportConfigMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json


/**
 * Import worker handles the import of files in a Worker.
 *
 * @param context               Context.
 * @param params                Worker parameters.
 * @param services              Set of all import services.
 * @param importConfigMapper    Mapper for the import config.
 */
@HiltWorker
internal class ImportWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val services: Set<@JvmSuppressWildcards ImportService>,
    private val importConfigMapper: ImportConfigMapper
): CoroutineWorker(context, params) {

    /**
     * JSON used to deserialize the import config.
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
            val id: String? = inputData.getString("import_id")
            val configJson: String? = inputData.getString("import_config")

            if (id == null || configJson == null) {
                return Result.failure()
            }

            val configDto: ImportConfigDto = json.decodeFromString<ImportConfigDto>(configJson)
            val config: ImportConfig = importConfigMapper.toDomain(configDto)

            //Get import service:
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
        catch (e: Exception) {
            when (e) {
                is InvalidKeyException -> {
                    //The decryption key is invalid:
                    return Result.failure(workDataOf("err_code" to "key_invalid"))
                }
                is CorruptImportFileException -> {
                    //Import file is corrupt:
                    return Result.failure(workDataOf("err_code" to "file_corrupt"))
                }
                is UnsupportedFileFormatException -> {
                    //Import file is corrupt:
                    return Result.failure(workDataOf("err_code" to "invalid_file_format"))
                }
                else -> {
                    //Some undefined issue occurred
                    return Result.failure()
                }
            }
        }
    }

}
