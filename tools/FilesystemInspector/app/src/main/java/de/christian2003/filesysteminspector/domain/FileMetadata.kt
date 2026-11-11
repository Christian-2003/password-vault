package de.christian2003.filesysteminspector.domain


data class FileMetadata(
    val name: String,
    val path: String,
    val size: Long?,
    val mimeType: String?,
    val fileType: String,
    val extension: String?,
    val lastModified: Long?,
    val isDirectory: Boolean,
    val canRead: Boolean,
    val canWrite: Boolean
)
