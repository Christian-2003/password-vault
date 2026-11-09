package de.christian2003.feature.export.domain.entities

import androidx.annotation.StringRes


/**
 * Export descriptor.
 *
 * @param id                    ID of the import service (e.g. "V3Backup" or "CSVImport").
 * @param importFileExtensions  Recommended file extension for the imported file (e.g. "xml" or "json").
 * @param importFileMimeTypes   Mime type for the imported file.
 * @param titleId               String resource containing the title for the importer.
 * @param subtitleId            String resource containing the subtitle for the importer.
 */
internal data class ImportDescriptor(
    val id: String,
    val importFileExtensions: Set<String>,
    val importFileMimeTypes: Set<String>,
    @param:StringRes val titleId: Int,
    @param:StringRes val subtitleId: Int
)
