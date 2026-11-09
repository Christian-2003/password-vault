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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


internal class BackupService @Inject constructor(
    private val v3BackupService: V3BackupService
) : ExportService, ImportService {

    /**
     * Descriptor for the export service.
     */
    override val exportDescriptor: ExportDescriptor = v3BackupService.exportDescriptor

    /**
     * Descriptor for the import service.
     */
    override val importDescriptor: ImportDescriptor = v3BackupService.importDescriptor


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
    override fun getProperties(importFile: Uri): Flow<ImportProperties?> {
        return v3BackupService.getProperties(importFile)
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
     */
    override fun beginImport(config: ImportConfig): Flow<Float> {
        return v3BackupService.beginImport(config)
    }

}
