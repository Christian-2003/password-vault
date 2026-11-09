package de.christian2003.feature.export.domain.exceptions


/**
 * Exception is thrown if the file format of the selected file for an import is unsupported.
 * Example: A file with the format ".xml" is selected. This file format does not correspond to any
 * format that can be imported. In such a case, this exception will be thrown.
 *
 * @param message   Message for the exception.
 */
internal class UnsupportedFileFormatException(
    message: String
): Exception(message) {

}
