package de.christian2003.feature.export.domain.entities


/**
 * Properties from a file to import.
 *
 * @param isEncrypted   Whether the file is encrypted.
 */
internal data class ImportProperties(
    val isEncrypted: Boolean
)
