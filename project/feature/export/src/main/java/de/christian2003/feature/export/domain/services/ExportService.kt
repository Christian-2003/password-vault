package de.christian2003.feature.export.domain.services

import de.christian2003.feature.export.domain.entities.ExportConfig
import kotlinx.coroutines.flow.Flow


/**
 * Service through which to create exports of data.
 */
internal interface ExportService {

    /**
     * Recommended file extension for the exported file (e.g. "zip", "txt" or "json").
     */
    val exportFileExtension: String

    /**
     * Whether the export can include accounts.
     */
    val canExportAccounts: Boolean

    /**
     * Whether the export can include files.
     */
    val canExportFiles: Boolean

    /**
     * Whether the export is encrypted. In this case it is required to provide an encryption key
     * seed in the export config. Otherwise, the encryption key seed is not required.
     */
    val isExportEncrypted: Boolean


    /**
     * Creates an export according to the provided configuration. The method starts a background
     * task and provides progress updates using the returned flow, which reports the progress as
     * a percentage between 0 % to 100 % (i.e. 0.0 to 1.0).
     *
     * @param config    Configuration for the export.
     * @return          Flow which reports the progress.
     */
    fun createExport(config: ExportConfig): Flow<Float>

}
