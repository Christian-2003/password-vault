package de.christian2003.feature.export.domain.entities

import androidx.annotation.StringRes


/**
 * Export descriptor.
 *
 * @param id                    ID of the export service (e.g. "xml" or "json").
 * @param exportFileExtension   Recommended file extension for the exported file (e.g. "xml" or "json").
 * @param exportFileMimeType    Mime type for the exported file.
 * @param canExportAccounts     Whether the export can include accounts.
 * @param canExportFiles        Whether the export can include files.
 * @param isExportEncrypted     Whether the export is encrypted. In this case it is required to
 *                              provide an encryption key seed in the export config. Otherwise, the
 *                              encryption key seed is not required.
 * @param titleId               String resource containing the title for the exporter.
 * @param subtitleId            String resource containing the subtitle for the exporter.
 * @param helpTextId            String resource containing the help text for the exporter.
 */
internal data class ExportDescriptor(
    val id: String,
    val exportFileExtension: String,
    val exportFileMimeType: String,
    val canExportAccounts: Boolean,
    val canExportFiles: Boolean,
    val isExportEncrypted: Boolean,
    @param:StringRes val titleId: Int,
    @param:StringRes val subtitleId: Int,
    @param:StringRes val helpTextId: Int
)
