package de.christian2003.feature.export.domain.exceptions


/**
 * Exception is thrown if the import file is corrupted / malformed and cannot be imported.
 *
 * @param message   Message for the exception.
 */
internal class CorruptImportFileException(
    message: String
): Exception(message) {

}
