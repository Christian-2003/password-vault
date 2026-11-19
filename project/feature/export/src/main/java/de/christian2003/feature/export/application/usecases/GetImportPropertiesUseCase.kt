package de.christian2003.feature.export.application.usecases

import android.net.Uri
import de.christian2003.feature.export.domain.entities.ImportProperties
import de.christian2003.feature.export.domain.services.ImportService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * Use case to retrieve the properties of an import file.
 *
 * @param services  Set of all import services.
 */
internal class GetImportPropertiesUseCase @Inject constructor(
    private val services: Set<@JvmSuppressWildcards ImportService>
) {

    /**
     * Retrieves the properties for the import file specified.
     *
     * @param id                        ID of the import service.
     * @param file                      URI to the file whose properties to determine.
     * @return                          Import properties or null if the properties cannot be determined.
     * @throws IllegalArgumentException The specified ID is not a known service.
     */
    suspend fun getProperties(id: String, file: Uri): ImportProperties = withContext(Dispatchers.IO) {
        val service: ImportService? = services.find { service -> service.importDescriptor.id == id }

        if (service == null) {
            throw IllegalArgumentException("'${id}' is not a known service")
        }

        val properties: ImportProperties = service.getProperties(file)
        return@withContext properties
    }

}
