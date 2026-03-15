package de.christian2003.data.files.domain.entities


/**
 * Value object models an internal file.
 *
 * @param internalName      Internal name of the file (example: "abc123def456.enc").
 * @param actualFileName    Actual cleartext file name (example: "Lawsuit.pdf").
 * @param metadata          Metadata for the file.
 */
data class InternalFile(
    val internalName: String,
    val actualFileName: String,
    val metadata: InternalFileMetadata = InternalFileMetadata()
)
