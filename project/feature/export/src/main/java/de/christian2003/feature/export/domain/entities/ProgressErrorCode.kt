package de.christian2003.feature.export.domain.entities


/**
 * Error codes for service progress.
 *
 * @property Ok                 No error.
 * @property InvalidKey         The provided encryption key seed is invalid.
 * @property CorruptFile        The file is corrupt.
 * @property InvalidFileFormat  The file format (i.e. extension) is unsupported.
 */
internal enum class ProgressErrorCode {

    Ok,
    InvalidKey,
    CorruptFile,
    InvalidFileFormat

}
