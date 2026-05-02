package de.christian2003.feature.export.domain.exceptions


/**
 * Exception is thrown if the export config provided to an export service is illegal.
 *
 * @param message   Message for the exception.
 */
internal class IllegalExportConfigException(
    message: String
): Exception(message) {

}
