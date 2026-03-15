package de.christian2003.data.files.domain.entities

import java.time.LocalDateTime


/**
 * Metadata for an internal file.
 *
 * @param createdAt         Timestamp at which the file was created.
 * @param editedAt          Timestamp at which the file was last edited.
 * @param accessedAt        Timestamp at which the file was last accessed (e.g. viewed).
 * @param sizeInKilobytes   Size of the file in kilo bytes (kB).
 */
data class InternalFileMetadata(
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val editedAt: LocalDateTime = LocalDateTime.now(),
    val accessedAt: LocalDateTime = LocalDateTime.now(),
    val sizeInKilobytes: Long = 0
)
