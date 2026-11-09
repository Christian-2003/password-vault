package de.christian2003.feature.export.domain.services

import android.net.Uri
import de.christian2003.feature.export.domain.entities.ImportConfig
import de.christian2003.feature.export.domain.entities.ImportDescriptor
import de.christian2003.feature.export.domain.entities.ImportProperties
import kotlinx.coroutines.flow.Flow
import de.christian2003.feature.export.domain.exceptions.CorruptImportFileException
import de.christian2003.feature.export.domain.exceptions.IllegalImportConfigException
import de.christian2003.feature.export.domain.exceptions.UnsupportedFileFormatException


internal interface ImportService {

    /**
     * Descriptor for the import service.
     */
    val importDescriptor: ImportDescriptor


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
    fun getProperties(importFile: Uri): Flow<ImportProperties?>


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
    fun beginImport(config: ImportConfig): Flow<Float>

}
