package de.christian2003.feature.export.infrastructure.backup

import android.net.Uri
import de.christian2003.feature.export.domain.entities.ExportConfig
import de.christian2003.feature.export.domain.entities.ExportDescriptor
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ImportDescriptor
import de.christian2003.feature.export.domain.entities.ImportProperties
import de.christian2003.feature.export.domain.services.ExportService
import de.christian2003.feature.export.domain.services.ImportService
import de.christian2003.feature.export.infrastructure.backup.v3.V3BackupService
import de.christian2003.feature.export.domain.exceptions.CorruptImportFileException
import de.christian2003.feature.export.domain.exceptions.IllegalImportConfigException
import de.christian2003.feature.export.domain.exceptions.InvalidKeyException
import de.christian2003.feature.export.domain.exceptions.UnsupportedFileFormatException
import de.christian2003.feature.export.infrastructure.backup.v2.V2BackupService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject


internal class BackupService @Inject constructor(
    private val v2BackupService: V2BackupService,
    private val v3BackupService: V3BackupService
) : ExportService, ImportService {

    /**
     * Descriptor for the export service.
     */
    override val exportDescriptor: ExportDescriptor = v3BackupService.exportDescriptor

    /**
     * Descriptor for the import service.
     */
    override val importDescriptor: ImportDescriptor = ImportDescriptor(
        id = "BackupService",
        importFileExtensions = v3BackupService.importDescriptor.importFileExtensions
                + v2BackupService.importDescriptor.importFileExtensions,
        importFileMimeTypes = v3BackupService.importDescriptor.importFileMimeTypes
                + v2BackupService.importDescriptor.importFileMimeTypes,
        titleId = v3BackupService.importDescriptor.titleId,
        subtitleId = v3BackupService.importDescriptor.subtitleId
    )


    /**
     * Creates an export according to the provided configuration. The method starts a background
     * task and provides progress updates using the returned flow, which reports the progress as
     * a percentage between 0 % to 100 % (i.e. 0.0 to 1.0).
     *
     * @param config    Configuration for the export.
     * @return          Flow which reports the progress.
     */
    override fun createExport(config: ExportConfig): Flow<Float> {
        return v3BackupService.createExport(config)
    }


    /**
     * Performs a preflight check of the provided import file to get additional data about the
     * file to import.
     *
     * @param importFile                        URI of the file to import for which to perform the
     *                                          preflight request.
     * @return                                  Response containing properties of the file to import.
     * @throws CorruptImportFileException       The file to import is corrupt and cannot be imported.
     * @throws UnsupportedFileFormatException   The selected file is unsupported and cannot be imported.
     */
    override suspend fun getProperties(importFile: Uri): ImportProperties {
        var importProperties: ImportProperties? = null

        //First, try V3:
        try {
            importProperties = v3BackupService.getProperties(importFile)
        }
        catch (_: Exception) { }

        //Next, try V2:
        if (importProperties == null) {
            try {
                importProperties = v2BackupService.getProperties(importFile)
            }
            catch (_: Exception) { }
        }

        //Finish:
        if (importProperties == null) {
            throw CorruptImportFileException("Cannot get properties from file")
        }
        return importProperties
    }

    /**
     * Begins an import according to the provided configuration. The method starts a background
     * task and provides progress updates using the returned flow, which reports the progress as
     * a percentage between 0 % to 100 % (i.e. 0.0 to 1.0).
     *
     * @param config                            Configuration for the import.
     * @return                                  Flow which reports the progress.
     * @throws CorruptImportFileException       The file to import is corrupt and cannot be imported.
     * @throws IllegalImportConfigException     The import config is illegal.
     * @throws UnsupportedFileFormatException   The selected file is unsupported and cannot be imported.
     * @throws InvalidKeyException              The provided decryption key seed is invalid.
     */
    override fun beginImport(config: ImportConfig): Flow<Float> = flow {
        //First, try V3:
        try {
            val v3Flow: Flow<Float> = v3BackupService.beginImport(config)
            v3Flow.collect { emit(it) }
        }
        catch (_: Exception) {
            //Next, try V2:
            try {
                val v2Flow: Flow<Float> = v2BackupService.beginImport(config)
                v2Flow.collect { emit(it) }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

}
