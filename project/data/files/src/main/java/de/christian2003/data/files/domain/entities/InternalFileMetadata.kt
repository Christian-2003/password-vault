package de.christian2003.data.files.domain.entities

import java.time.LocalDateTime


/**
 * Metadata for an internal file.
 *
 * @param mimeType      Mime type of the internal file, which is determined by it's file type.
 * @param createdAt     Timestamp at which the file was created.
 * @param editedAt      Timestamp at which the file was last edited.
 * @param accessedAt    Timestamp at which the file was last accessed (e.g. viewed).
 * @param size          Size of the file in bytes.
 */
data class InternalFileMetadata(
    val mimeType: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val editedAt: LocalDateTime = LocalDateTime.now(),
    val accessedAt: LocalDateTime = LocalDateTime.now(),
    val size: Long = 0
)
