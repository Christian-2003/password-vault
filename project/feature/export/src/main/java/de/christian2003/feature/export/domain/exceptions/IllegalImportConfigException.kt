package de.christian2003.feature.export.domain.exceptions


/**
 * Exception is thrown if the import config provided to an import service is illegal.
 *
 * @param message   Message for the exception.
 */
internal class IllegalImportConfigException(
    message: String
): Exception(message) {

}
