package de.christian2003.feature.export.domain.exceptions


/**
 * Exception that can be thrown by an import / export service if the provided key seed (i.e. the
 * password entered by the user for the export file) is invalid.
 *
 * @param message   Message for the exception.
 */
internal class InvalidKeyException(
    message: String
): Exception(message) {

}
