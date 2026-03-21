package de.christian2003.data.files.domain.entities

import java.time.LocalDateTime


/**
 * Value object for the metadata of files that are shared.
 *
 * @param actualFileName    Actual name of the file which is shared.
 * @param internalFileName  Internal name of the file that is shared.
 * @param timestamp         Timestamp at which the file was shared.
 */
internal data class SharedFileMetadata(
    val actualFileName: String,
    val internalFileName: String,
    val timestamp: LocalDateTime
)
